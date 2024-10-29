package zerobase.sijak.jwt;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException, ServletException {

        log.info("servletPath: {}", request.getServletPath());
        String servletPath = request.getServletPath();
        // 로그인, RefreshToken 재발급 요청은 JWT 토큰 검사하지 않는다.
        if (servletPath.equals("/api/login") || servletPath.equals("/api/reissue")) {
            filterChain.doFilter(request, response);
        }
        // http에서 뽑아온 인증 정보
        log.info("doFilter 토큰 검증 시작 request: {}, response: {}", request, response);
        String token = resolveToken(request);
        log.info("doFilter token: {}", token);
        // 검증: 해당 정보가 있고 해당 토큰의 claims 정보가 옳바를 경우
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
        log.info("doFilter 토큰 검증 완료!");
    }


    //http header 에 있는 인증 정보 부분에서 해당 토큰의 "bearer" 이후 정보만 뽑아 오는 메서드
    private String resolveToken(HttpServletRequest request) {
        log.info("resolve 토큰 검증 request: {}", request);
        String bearerToken = request.getHeader(TOKEN_HEADER);

        log.info("resolve bearerToken: {}", bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            log.info("resolve 토큰 검증 완료!");
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
