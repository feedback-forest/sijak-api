package zerobase.sijak.service;

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
import zerobase.sijak.SijakApplication;
import zerobase.sijak.dto.MyPageParam;
import zerobase.sijak.dto.kakao.*;
import zerobase.sijak.exception.EmailNotExistException;
import zerobase.sijak.exception.ErrorCode;
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
                    .ageRange(profile.getKakao_account().getAge_range()).build();

            boolean isAlreadySaved = alreadySavedUserJudge(kakaoUserInfo.getEmail());

            if (isAlreadySaved) {
                KakaoUser kakaoUser = new KakaoUser(kakaoUserInfo);
                Member member = new Member(kakaoUserInfo);
                kakaoRepository.save(kakaoUser);
                memberRepository.save(member);
                log.info("사용자 정보 DB 저장 성공");
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


    public Member getMyPage(String token) {

        String jwtToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseClaims(jwtToken);
        log.info("email : {}", claims.getSubject());

        Member member = memberRepository.findByAccountEmail(claims.getSubject());
        if (member == null) {
            throw new EmailNotExistException("유저 email이 존재하지 않습니다.", ErrorCode.EMAIL_NOT_EXIST);
        }

        return memberRepository.findByAccountEmail(claims.getSubject());
    }


    // 위치 기반으로 데이터를 저장 -> 수정 필요
    public Member updateMyPage(MyPageParam myPageParam) {
        return new Member();
    }
}
