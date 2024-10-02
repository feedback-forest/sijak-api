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
import zerobase.sijak.exception.AlreadyNicknameExistException;
import zerobase.sijak.exception.EmailNotExistException;
import zerobase.sijak.exception.ErrorCode;
import zerobase.sijak.exception.InvalidNicknameException;
import zerobase.sijak.jwt.JwtTokenProvider;
import zerobase.sijak.jwt.KakaoUserService;
import zerobase.sijak.jwt.KakaoUser;
import zerobase.sijak.persist.domain.Member;
import zerobase.sijak.persist.repository.KakaoRepository;
import zerobase.sijak.persist.repository.MemberRepository;

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

    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoUserService kakaoUserService;
    private final KakaoRepository kakaoRepository;
    private final MemberRepository memberRepository;
    private WebClient.Builder webClientBuilder;
    private static final Logger logger = LoggerFactory.getLogger(SijakApplication.class);

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

            boolean isAlreadySaved = alreadySavedUserJudge(kakaoUserInfo.getEmail());

            if (isAlreadySaved) {
                KakaoUser kakaoUser = new KakaoUser(kakaoUserInfo);
                Member member = new Member(kakaoUserInfo);
                kakaoRepository.save(kakaoUser);
                memberRepository.save(member);
                log.info("첫 로그인 : 사용자 정보 DB 저장 성공");
            }
            TokenDTO tokenDTO = kakaoUserService.login(kakaoUserInfo.getEmail(), kakaoUserInfo.getName());

            return new ResponseDTO(tokenDTO, isAlreadySaved);
        }
        return null;
    }


    private boolean alreadySavedUserJudge(String email) {

        Member member = memberRepository.findByAccountEmail(email);
        return member == null;
    }


    public MyPageResponse getMyPage(String token) {

        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            throw new EmailNotExistException("해당 유저 email이 존재하지 않습니다.", ErrorCode.EMAIL_NOT_EXIST);
        }

        String jwtToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseClaims(jwtToken);
        log.info("email : {}", claims.getSubject());

        Member member = memberRepository.findByAccountEmail(claims.getSubject());
        if (member == null) {
            throw new EmailNotExistException("유저 email이 존재하지 않습니다.", ErrorCode.EMAIL_NOT_EXIST);
        }


        MyPageResponse myPageResponse = MyPageResponse.builder()
                .nickname(member.getProfileNickname())
                .location(member.getAddress())
                .email(member.getAccountEmail())
                .phoneNumber(member.getPhoneNumber())
                .birth(member.getBirth())
                .gender(member.getGender()).build();

        return myPageResponse;
    }


    // 위치 기반으로 데이터를 저장 -> 수정 필요
    public void updateMyPage(String token, MyPageParam myPageParam) {

        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            throw new EmailNotExistException("해당 유저 email이 존재하지 않습니다.", ErrorCode.EMAIL_NOT_EXIST);
        }

        String jwtToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseClaims(jwtToken);
        log.info("email : {}", claims.getSubject());
        Member member = memberRepository.findByAccountEmail(claims.getSubject());
        if (member == null) {
            throw new EmailNotExistException("유저 email이 존재하지 않습니다.", ErrorCode.EMAIL_NOT_EXIST);
        }

        member.setProfileNickname(myPageParam.getNickname());
        member.setAddress(myPageParam.getAddress());

        memberRepository.save(member);
    }


    public void validateNickname(NicknameRequest nicknameRequest) {
        String nickname = nicknameRequest.getNickname();

        if (nickname == null || nickname.isEmpty())
            throw new InvalidNicknameException("띄어쓰기 없이 2자 ~ 12자까지 가능해요.", ErrorCode.INVALID_NICKNAME);
        if (nickname.length() < 2 || nickname.length() > 12)
            throw new InvalidNicknameException("띄어쓰기 없이 2자 ~ 12자까지 가능해요.", ErrorCode.INVALID_NICKNAME);
        if (!nickname.matches("^[가-힣a-zA-Z0-9]+$"))
            throw new InvalidNicknameException("띄어쓰기 없이 2자 ~ 12자까지 가능해요.", ErrorCode.INVALID_NICKNAME);
        if (memberRepository.existsByProfileNickname(nickname))
            throw new AlreadyNicknameExistException("이미 사용중인 닉네임이예요. 다른 닉네임을 적어주세요.", ErrorCode.ALREADY_NICKNAME_EXIST);
    }

    public void setNickname(String token, NicknameRequest nicknameRequest) {

        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            throw new EmailNotExistException("해당 유저 email이 존재하지 않습니다.", ErrorCode.EMAIL_NOT_EXIST);
        }

        String nickname = nicknameRequest.getNickname();

        String jwtToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseClaims(jwtToken);
        log.info("email : {}", claims.getSubject());

        Member member = memberRepository.findByAccountEmail(claims.getSubject());
        if (member == null) {
            throw new EmailNotExistException("유저 email이 존재하지 않습니다.", ErrorCode.EMAIL_NOT_EXIST);
        }

        validateNickname(NicknameRequest.builder().nickname(nickname).build());

        member.setProfileNickname(nickname);
        memberRepository.save(member);
    }

    public void updateAddress(String token, PositionInfo positionInfo) throws JsonProcessingException {
        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            throw new EmailNotExistException("해당 유저 email이 존재하지 않습니다.", ErrorCode.EMAIL_NOT_EXIST);
        }

        String jwtToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseClaims(jwtToken);
        log.info("email : {}", claims.getSubject());

        Member member = memberRepository.findByAccountEmail(claims.getSubject());
        if (member == null) {
            throw new EmailNotExistException("유저 email이 존재하지 않습니다.", ErrorCode.EMAIL_NOT_EXIST);
        }

        double latitude = positionInfo.getLatitude();
        double longitude = positionInfo.getLongitude();

        member.setLongitude(longitude);
        member.setLatitude(latitude);
        String address = getNewAddress(longitude, latitude);
        member.setAddress(address);

        memberRepository.save(member);
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
        }

        return "";
    }
}
