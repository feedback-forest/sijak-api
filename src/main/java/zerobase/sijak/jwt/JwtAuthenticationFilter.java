package zerobase.sijak.jwt;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // http에서 뽑아온 인증 정보
        log.info("토큰 검증 시작 request: {}, response: {}", request, response);
        String token = resolveToken((HttpServletRequest) request);
        log.info("token: {}", token);
        //해당 정보가 있고 해당 토큰의 claims 정보가 옳바를 경우
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
        log.info("토큰 검증 완료!");
    }


    //http header 에 있는 인증 정보 부분에서 해당 토큰의 "bearer" 이후 정보만 뽑아 오는 메서드
    private String resolveToken(HttpServletRequest request) {
        log.info("토큰 검증 request: {}", request);
        String bearerToken = request.getHeader("Authorization");
        log.info("bearerToken: {}", bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        log.info("토큰 검증 완료!!");
        return null;
    }
}
