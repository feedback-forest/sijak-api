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

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
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

        long now = (new Date()).getTime();
        //만기 시간 설정
        Date accessTokenExpiresIn = new Date(now + 86400000); // 24시간

        log.info("Access 토큰 생성 시작!");
        //접근 토큰 생성
        String accessToken = Jwts.builder().setSubject(authentication.getName())
                .claim("auth", authorities)
                .setIssuer("sijak")
                .setIssuedAt(new Date(now))
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("Access 토큰 생성 완료!");
        log.info("Refresh 토큰 생성 시작!");
        //리프레쉬 토큰 생성
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuer("sijak")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 1209600000)) // 14일
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("Refresh 토큰 생성 완료!");

        return TokenDTO.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 접근 토큰을 이용하여 권한 정보를 받아오는 메서드
    public Authentication getAuthentication(String accessToken) {
        //토큰 복호화

        Claims claims = parseClaims(accessToken);
        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰");
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
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
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
}

