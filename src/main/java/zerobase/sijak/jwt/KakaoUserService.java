package zerobase.sijak.jwt;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import zerobase.sijak.dto.kakao.TokenDTO;
import zerobase.sijak.dto.kakao.TokenRequest;
import zerobase.sijak.exception.Code;
import zerobase.sijak.exception.CustomException;
import zerobase.sijak.persist.domain.RefreshToken;
import zerobase.sijak.persist.repository.RefreshTokenRepository;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class KakaoUserService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenDTO login(String kakaoUserId, String nickname) {
        log.info("사용자 정보를 토대로 토큰 생성 kakaoUserID: {}, nickname: {}", kakaoUserId, nickname);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(kakaoUserId, nickname);
        log.info("authenticationToken: {}", authenticationToken);

        log.info("getObject: {}", authenticationManagerBuilder.getObject());
        log.info("authenticate: {}", authenticationManagerBuilder.getObject().authenticate(authenticationToken));

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("authentication: {}", authentication);

        TokenDTO tokenDTO = jwtTokenProvider.generateToken(authentication);

        // RefreshToken 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDTO.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        log.info("사용자 정보를 토대로 토큰 생성 완료 token: {}", tokenDTO);
        log.info("access : {}", tokenDTO.getAccessToken());
        log.info("refresh: {}", tokenDTO.getRefreshToken());
        log.info("grant type: {}", tokenDTO.getGrantType());
        return tokenDTO;
    }

    public TokenDTO reissue(TokenRequest tokenRequest, HttpServletResponse response) {

        log.info("access: {}", tokenRequest.getAccessToken());
        log.info("refresh: {}", tokenRequest.getRefreshToken());

        // 1. Refersh Token 검증
        if (!jwtTokenProvider.validateToken(tokenRequest.getRefreshToken())) {
            throw new CustomException(Code.REFRESH_TOKEN_UNAUTHORIZED);
        }

        //2. Access Token 에서 UserDetails 객체 가져오기
        Authentication authentication = jwtTokenProvider.getAuthentication(tokenRequest.getAccessToken());

        //3. Access Token 에서 kakaoUserId 가져오고, set
        String KakaoUserId = jwtTokenProvider.getKakaoUserId(tokenRequest.getAccessToken());

        log.info("kakaoUserId: {}", KakaoUserId);

        // 4. 저장소에서 Kakao User ID 를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findById(authentication.getName())
                .orElseThrow(() -> new CustomException(Code.EXPIRED_REFRESH_TOKEN));

        // 5. Refresh Token 일치 검사
        if (!refreshToken.getValue().equals(tokenRequest.getRefreshToken())) {
            throw new CustomException(Code.REFRESH_TOKEN_UNMATCHED);
        }

        // 6. 새로운 토큰 생성
        TokenDTO tokenDto = jwtTokenProvider.generateToken(authentication);

        // 7. 저장소 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());

        refreshTokenRepository.save(newRefreshToken);

        // 8. 토큰 발급
        return tokenDto;

    }
}
