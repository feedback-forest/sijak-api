package zerobase.sijak.jwt;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import zerobase.sijak.dto.kakao.TokenDTO;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class KakaoUserService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenDTO login(String email, String nickname) {
        log.info("사용자 정보를 토대로 토큰 생성 email: {}, nickname: {}", email, nickname);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, nickname);
        log.info("authenticationToken: {}", authenticationToken);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("authentication: {}", authentication);

        TokenDTO tokenDTO = jwtTokenProvider.generateToken(authentication);
        log.info("사용자 정보를 토대로 토큰 생성 완료 token: {}", tokenDTO);
        return tokenDTO;
    }
}
