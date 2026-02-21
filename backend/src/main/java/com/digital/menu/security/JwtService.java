package com.digital.menu.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    @Value("${app.jwt.secret:}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-seconds:43200}")
    private long expirationSeconds;

    @PostConstruct
    public void validateSecret() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException("app.jwt.secret must be configured");
        }
        try {
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            if (keyBytes.length < 32) {
                throw new IllegalStateException("app.jwt.secret must decode to at least 32 bytes");
            }
        } catch (Exception ex) {
            throw new IllegalStateException("app.jwt.secret must be valid base64", ex);
        }
    }

    public String generateToken(AdminUserPrincipal principal) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setSubject(principal.getUsername())
            .addClaims(Map.of("tenantId", principal.getTenantId(), "role", principal.getAuthorities().iterator().next().getAuthority()))
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(expirationSeconds)))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractTenantId(String token) {
        return extractAllClaims(token).get("tenantId", String.class);
    }

    public boolean isTokenValid(String token, AdminUserPrincipal principal) {
        String username = extractUsername(token);
        return username.equals(principal.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiry = extractAllClaims(token).getExpiration();
        return expiry.before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
