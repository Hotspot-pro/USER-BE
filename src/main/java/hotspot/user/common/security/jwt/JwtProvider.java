package hotspot.user.common.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import hotspot.user.common.security.PrincipalDetails;
import hotspot.user.member.domain.FamilyRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 JWT 토큰 생성, 검증 및 Authentication 객체 변환
 */
@Slf4j
@Component
public class JwtProvider {

    private final String secretKey;
    private final long accessTokenExpirationTime;
    private final long refreshTokenExpirationTime;
    private SecretKey key;

    public JwtProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-expiration}") long accessTokenExpirationTime,
            @Value("${jwt.refresh-expiration}") long refreshTokenExpirationTime) {
        this.secretKey = secretKey;
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
    }

    // 시크릿 키 String -> SecretKey로 변환
    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, accessTokenExpirationTime, true);
    }

    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, refreshTokenExpirationTime, false);
    }

    private String createToken(Authentication authentication, long expirationTime, boolean isAccessToken) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expirationTime);

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

        var builder = Jwts.builder()
                .header().add("typ", "JWT").and()
                .claim("memberId", principal.getId())
                .subject(authentication.getName())
                .issuedAt(now)
                .expiration(expiredDate)
                .signWith(key)
                .id(UUID.randomUUID().toString());

        if (isAccessToken) {
            // FamilyRole 명칭(예: OWNER) 저장
            builder.claim("role", principal.getRole().name());
        }

        return builder.compact();
    }

    // 토큰에서 인증 정보 추출
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        String email = claims.getSubject();
        Long memberId = claims.get("memberId", Long.class);
        String roleStr = claims.get("role", String.class);

        if (roleStr == null) {
            roleStr = "CHILD"; // 기본값
        }

        PrincipalDetails principal = new PrincipalDetails(memberId, email, FamilyRole.valueOf(roleStr));

        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }

    // Request Header에서 토큰 추출 (JwtFilter에서 호출될 메서드)
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
        }
        return false;
    }

    // 토큰 파싱
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
