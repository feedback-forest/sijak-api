package zerobase.sijak.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import zerobase.sijak.SijakApplication;
import zerobase.sijak.dto.*;
import zerobase.sijak.dto.kakao.*;
import zerobase.sijak.exception.*;
import zerobase.sijak.jwt.JwtTokenProvider;
import zerobase.sijak.jwt.KakaoUserService;
import zerobase.sijak.jwt.KakaoUser;
import zerobase.sijak.persist.domain.Member;
import zerobase.sijak.persist.domain.Term;
import zerobase.sijak.persist.domain.TermType;
import zerobase.sijak.persist.repository.KakaoRepository;
import zerobase.sijak.persist.repository.MemberRepository;
import zerobase.sijak.persist.repository.TermRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KakaoService {

    @Value("${client-id}")
    private String CLIENT_ID;

    @Value("${redirect-uri}")
    private String REDIRECT_URI;

    @Value("${client-secret}")
    private String CLIENT_SECRET;

    @Value("${token_uri}")
    private String TOKEN_URI;

    @Value("${user_info_uri}")
    private String USER_INFO_URI;

    @Value("${logout_redirect_uri}")
    private String LOGOUT_REDIRECT_URI;

    @Value("${kakao_logout_uri}")
    private String KAKAO_LOGOUT_URI;

    @Value("${public_logout_uri}")
    private String PUBLIC_LOGOUT_URI;

    @Value("${admin_key}")
    private String ADMIN_KEY;

    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoUserService kakaoUserService;
    private final KakaoRepository kakaoRepository;
    private final MemberRepository memberRepository;
    private final TermRepository termRepository;
    private WebClient.Builder webClientBuilder;
    private static final Logger logger = LoggerFactory.getLogger(SijakApplication.class);

    public void logout(String token) {
        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            throw new CustomException(Code.KAKAO_ID_NOT_EXIST);
        }

        String jwtToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseClaims(jwtToken);
        log.info("kakaoUserID : {}", claims.getSubject());

        Member member = memberRepository.findByKakaoUserId(claims.getSubject()).orElseThrow(
                () -> new CustomException(Code.KAKAO_ID_NOT_EXIST)
        );

        String kakaoId = member.getKakaoUserId();

        WebClient webClient = WebClient.create(PUBLIC_LOGOUT_URI);
        log.info("webClient : {}", webClient.toString());
        webClient.post()
                .uri(PUBLIC_LOGOUT_URI)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "KakaoAK " + ADMIN_KEY)
                .bodyValue("target_id_type=user_id&target_id=" + kakaoId)
                .retrieve();
    }

    public ResponseDTO createPrivateToken(String code) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", CLIENT_ID);
        params.add("redirect_uri", REDIRECT_URI);
        params.add("code", code);
        params.add("client_secret", CLIENT_SECRET);

        WebClient wc = WebClient.create(TOKEN_URI);

        logger.info("카카오 Access 토큰을 요청하는 중...");

        // 서버 간 통신
        OAuthToken oauthToken = wc.post()
                .uri(TOKEN_URI)
                .body(BodyInserters.fromFormData(params))
                .header("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .bodyToMono(OAuthToken.class).block();

        log.info("카카오 Access 토큰 발급 완료 !");

        log.info("Access toke = {}", oauthToken.getAccess_token());
        log.info("카카오 Access 토큰 활용 -> 사용자 정보 조회");

        KakaoProfile profile = wc
                .get()
                .uri(USER_INFO_URI)
                .headers(headers -> headers.setBearerAuth(oauthToken.getAccess_token()))
                .retrieve()
                .bodyToMono(KakaoProfile.class)
                .block();

        log.info("카카오 Access 토큰 활용 -> 사용자 정보 조회 성공");

        log.info("사용자 정보 DB 저장 시작");

        if (profile != null) {
            KakaoUserInfo kakaoUserInfo = KakaoUserInfo.builder()
                    .kakaoUserId(profile.getId())
                    .nickname(profile.getProperties().getNickname())
                    .profileImageUrl(profile.getProperties().getProfile_image())
                    .birth(profile.getKakao_account().getBirthyear() + profile.getKakao_account().getBirthday())
                    .name(profile.getKakao_account().getName())
                    .email(profile.getKakao_account().getEmail())
                    .gender(profile.getKakao_account().getGender())
                    .ageRange(profile.getKakao_account().getAge_range())
                    .phoneNumber(profile.getKakao_account().getPhone_number()).build();

            boolean isAlreadySaved = alreadySavedUserJudge(String.valueOf(kakaoUserInfo.getKakaoUserId()));

            if (isAlreadySaved) {
                Member member = new Member(kakaoUserInfo);
                memberRepository.save(member);
                log.info("첫 로그인 : 사용자 정보 DB 저장 성공");
            }
            TokenDTO tokenDTO = kakaoUserService.login(String.valueOf(kakaoUserInfo.getKakaoUserId()), kakaoUserInfo.getNickname());

            return new ResponseDTO(tokenDTO, isAlreadySaved);
        }
        return null;
    }


    private boolean alreadySavedUserJudge(String kakaoUserId) {
        Optional<Member> member = memberRepository.findByKakaoUserId(kakaoUserId);
        return member.isEmpty();
    }


    public MyPageResponse getMyPage(String token) {

        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            throw new CustomException(Code.KAKAO_ID_NOT_EXIST);
        }

        String jwtToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseClaims(jwtToken);
        log.info("kakaoUserId : {}", claims.getSubject());

        Member member = memberRepository.findByKakaoUserId(claims.getSubject()).orElseThrow(
                () -> new CustomException(Code.KAKAO_ID_NOT_EXIST)
        );


        MyPageResponse myPageResponse = MyPageResponse.builder()
                .nickname(member.getProfileNickname())
                .location(member.getAddress())
                .ageRange(member.getAgeRange())
                .email(member.getAccountEmail())
                .phoneNumber(member.getPhoneNumber())
                .birth(member.getBirth())
                .gender(member.getGender()).build();

        return myPageResponse;
    }


    // 위치 기반으로 데이터를 저장 -> 수정 필요
    public void updateMyPage(String token, MyPageParam myPageParam) {

        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            throw new CustomException(Code.KAKAO_ID_NOT_EXIST);
        }

        String jwtToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseClaims(jwtToken);
        log.info("kakaoUserId : {}", claims.getSubject());
        Member member = memberRepository.findByKakaoUserId(claims.getSubject()).orElseThrow(
                () -> new CustomException(Code.KAKAO_ID_NOT_EXIST)
        );

        member.setProfileNickname(myPageParam.getNickname());
        member.setAddress(myPageParam.getAddress());

        memberRepository.save(member);
    }

    public void validateNickname(String token, NicknameRequest nicknameRequest) {

        String nickname = nicknameRequest.getNickname();

        // 회원이 아닌 경우
        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            if (nickname == null || nickname.isEmpty())
                throw new CustomException(Code.INVALID_LENGTH_NICKNAME);
            if (nickname.length() < 2 || nickname.length() > 12)
                throw new CustomException(Code.INVALID_LENGTH_NICKNAME);
            if (nickname.matches(".*[ㄱ-ㅎㅏ-ㅣ].*"))
                throw new CustomException(Code.INVALID_CV_NICKNAME);
            if (!nickname.matches("^[가-힣a-zA-Z0-9]+$"))
                throw new CustomException(Code.INVALID_CHARACTER_NICKNAME);
            if (memberRepository.existsByProfileNickname(nickname))
                throw new CustomException(Code.ALREADY_NICKNAME_EXIST);
        }
        // 회원인 경우
        else {
            String jwtToken = token.substring(7);
            Claims claims = jwtTokenProvider.parseClaims(jwtToken);
            log.info("email : {}", claims.getSubject());

            Member member = memberRepository.findByKakaoUserId(claims.getSubject()).orElseThrow(
                    () -> new CustomException(Code.KAKAO_ID_NOT_EXIST)
            );

            String pn = member.getProfileNickname();
            if (nickname.equals(pn)) return;
            if (nickname == null || nickname.isEmpty())
                throw new CustomException(Code.INVALID_LENGTH_NICKNAME);
            if (nickname.length() < 2 || nickname.length() > 12)
                throw new CustomException(Code.INVALID_LENGTH_NICKNAME);
            if (nickname.matches(".*[ㄱ-ㅎㅏ-ㅣ].*"))
                throw new CustomException(Code.INVALID_CV_NICKNAME);
            if (!nickname.matches("^[가-힣a-zA-Z0-9]+$"))
                throw new CustomException(Code.INVALID_CHARACTER_NICKNAME);
            if (memberRepository.existsByProfileNickname(nickname))
                throw new CustomException(Code.ALREADY_NICKNAME_EXIST);
        }

    }

    public void validateNickname(NicknameRequest nicknameRequest, String curNickname) {

        String nickname = nicknameRequest.getNickname();

        if (nickname.equals(curNickname)) return;
        if (nickname == null || nickname.isEmpty())
            throw new CustomException(Code.INVALID_LENGTH_NICKNAME);
        if (nickname.length() < 2 || nickname.length() > 12)
            throw new CustomException(Code.INVALID_LENGTH_NICKNAME);
        if (!nickname.matches("^[가-힣a-zA-Z0-9]+$"))
            throw new CustomException(Code.INVALID_CHARACTER_NICKNAME);
        if (memberRepository.existsByProfileNickname(nickname))
            throw new CustomException(Code.ALREADY_NICKNAME_EXIST);
    }

    public void setNickname(String token, SignUpRequest signUpRequest) {

        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            throw new CustomException(Code.KAKAO_ID_NOT_EXIST);
        }

        String jwtToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseClaims(jwtToken);
        log.info("email : {}", claims.getSubject());

        Member member = memberRepository.findByKakaoUserId(claims.getSubject()).orElseThrow(
                () -> new CustomException(Code.KAKAO_ID_NOT_EXIST)
        );

        String nickname = signUpRequest.getNickname();
        String pn = member.getProfileNickname();
        validateNickname(NicknameRequest.builder().nickname(nickname).build(), pn);

        member.setAgeRange(signUpRequest.getAgeRange());
        member.setGender(signUpRequest.getGender());
        member.setProfileNickname(nickname);
        memberRepository.save(member);
    }

    public GeoResponse updateAddress(String token, PositionInfo positionInfo) throws JsonProcessingException {
        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            throw new CustomException(Code.KAKAO_ID_NOT_EXIST);
        }

        String jwtToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseClaims(jwtToken);
        log.info("email : {}", claims.getSubject());

        Member member = memberRepository.findByKakaoUserId(claims.getSubject()).orElseThrow(
                () -> new CustomException(Code.KAKAO_ID_NOT_EXIST)
        );

        double latitude = positionInfo.getLatitude();
        double longitude = positionInfo.getLongitude();

        member.setLongitude(longitude);
        member.setLatitude(latitude);
        String address = getNewAddress(longitude, latitude);
        member.setAddress(address);

        memberRepository.save(member);

        return new GeoResponse(address);
    }

    public MyPageResponse getUserMypage(String token, int id) {
        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            throw new CustomException(Code.KAKAO_ID_NOT_EXIST);
        }

        String jwtToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseClaims(jwtToken);
        log.info("email : {}", claims.getSubject());

        Member member = memberRepository.findByIdAndKakaoUserId(id, claims.getSubject()).orElseThrow(
                () -> new CustomException(Code.KAKAO_ID_NOT_EXIST)
        );

        MyPageResponse myPageResponse = MyPageResponse.builder()
                .nickname(member.getProfileNickname())
                .location(member.getAddress())
                .ageRange(member.getAgeRange())
                .email(member.getAccountEmail())
                .phoneNumber(member.getPhoneNumber())
                .birth(member.getBirth())
                .gender(member.getGender()).build();

        return myPageResponse;
    }

    private String getNewAddress(double longitude, double latitude) throws JsonProcessingException {
        String kakaoGeo = "/v2/local/geo/coord2address.json";

        WebClient webClient = WebClient.builder()
                .baseUrl("https://dapi.kakao.com").build();

        Mono<String> stringMono = webClient.get()
                .uri(uribuilder -> uribuilder
                        .path(kakaoGeo)
                        .queryParam("x", longitude)
                        .queryParam("y", latitude)
                        .queryParam("input_coord", "WGS84")
                        .build())
                .header("Authorization", "KakaoAK " + CLIENT_ID)
                .retrieve()
                .bodyToMono(String.class);

        String response = stringMono.block();
        log.info("response : {}", response);
        ObjectMapper objectMapper = new ObjectMapper();
        GeoInfo geoInfo = objectMapper.readValue(response, GeoInfo.class);

        if (geoInfo != null && !geoInfo.documents.isEmpty()) {
            log.info("getInfo.documents.get(0) = {}", geoInfo.documents.get(0));
            String[] longAddress = geoInfo.documents.get(0).address.address_name.split(" ");
            return longAddress[0] + " " + longAddress[1];
        } else {
            throw new CustomException(Code.GEOLOCATION_NOT_EXIST);
        }
    }

    public void saveAgree(String token, AgreeInfo agreeInfo) {

        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            throw new CustomException(Code.KAKAO_ID_NOT_EXIST);
        }

        String jwtToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseClaims(jwtToken);
        log.info("email : {}", claims.getSubject());

        Member member = memberRepository.findByKakaoUserId(claims.getSubject()).orElseThrow(
                () -> new CustomException(Code.KAKAO_ID_NOT_EXIST)
        );

        List<String> agreeItems = agreeInfo.getAgreeItems();

        List<Term> terms = termRepository.findByMemberId(member.getId());

        for (Term term : terms) {
            term.setActive(agreeItems.contains(term.getName()));
            term.setUpdated_at(LocalDateTime.now());
            termRepository.save(term);
        }

        // 새로운 약관을 추가 (사용자가 동의한 약관 중 기존에 없는 경우)
        for (String agreeItem : agreeItems) {
            if (terms.stream().noneMatch(t -> t.getName().equals(agreeItem))) {
                Term term = Term.builder()
                        .member(member)
                        .created_at(LocalDateTime.now())
                        .updated_at(LocalDateTime.now())
                        .type(Objects.equals(agreeItem, "marketingAgree") ? TermType.optional : TermType.mandatory)
                        .name(agreeItem)
                        .active(true) // 새로 추가하는 항목은 active를 true로 설정
                        .build();

                termRepository.save(term); // 새로운 약관 저장
            }
        }


    }
}
