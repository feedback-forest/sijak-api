package zerobase.sijak.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import zerobase.sijak.dto.kakao.TokenDTO;
import zerobase.sijak.exception.Code;
import zerobase.sijak.exception.CustomException;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String BEARER_TYPE = "Bearer";
    private static final long TOKEN_EXPIRE_TIME = 1000 * 15;
    private static final long TOKEN_REFRESH_TIME = 1000 * 60 * 60 * 24;
    private final Key key;

    //생성자를 통하여 KEY 값을 BASE64로 디코딩(해석)하고 해석한 값을
    //SecretKey instance에 HMAC-SHA 로 암호화하여 초기화
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        log.info("key 암호화 시도");
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        log.info("key 암호화 성공");
    }

    //권한 값을 인자로 받아와, 각각
    public TokenDTO generateToken(Authentication authentication) {
        //권한 값을 합침
        log.info("토큰 생성 시작");
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        //만기 시간 설정
        Date accessTokenExpiresIn = new Date(now.getTime() + TOKEN_EXPIRE_TIME);
        Date refreshTokenExpiresIn = new Date(now.getTime() + TOKEN_REFRESH_TIME);

        log.info("Access 토큰 생성 시작!");
        /**
         * Access Token 생성
         *  header "alg" : "HS256"
         *  payload "auth": "ROLE_USER"
         *  payload "sub": kakao_id
         *  payload "iss": "sijak"
         *  payload "iat": 토큰 발급 시간
         *  payload "exp" : 토큰 만료 시간
         */
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setIssuer("sijak")
                .setIssuedAt(now)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("Access 토큰 생성 완료!");
        log.info("Refresh 토큰 생성 시작!");
        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("Refresh 토큰 생성 완료!");

        return TokenDTO.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();
    }

    // 접근 토큰을 이용하여 권한 정보를 받아오는 메서드
    public Authentication getAuthentication(String accessToken) {
        //토큰 복호화
        Claims claims = parseClaims(accessToken);
        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);

    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.", e);
            throw new CustomException(Code.INVALID_ACCESS_TOKEN);
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.", e);
            throw new CustomException(Code.EXPIRED_ACCESS_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.", e);
            throw new CustomException(Code.UNSUPPORTED_ACCESS_TOKEN);
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.", e);
            throw new CustomException(Code.WRONG_TYPE_ACCESS_TOKEN);
        }
    }

    //토큰을 claims로 변환하는 메서드
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }


    public String getKakaoUserId(String token) {
        String kakaoUserId = parseClaims(token).getSubject();
        return kakaoUserId;
    }

}

