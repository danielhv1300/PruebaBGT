package com.test.btg.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    private final Key key;
    private final long expirationMs;

    public JwtUtils(@Value("${jwt.secret}") String secret,
                    @Value("${jwt.expirationMs}") long expirationMs) {
        try {
            byte[] keyBytes = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            // Ensure key is at least 256 bits for HS256; if not, derive SHA-256 of the secret
            if (keyBytes.length < 32) {
                java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
                keyBytes = digest.digest(keyBytes);
            }
            this.key = Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create JWT signing key", e);
        }
        this.expirationMs = expirationMs;
    }

    public String generateTokenFromEmail(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)

                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromJwt(String token) {
        token = normalizeToken(token);
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            String token = normalizeToken(authToken);
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        }

        return false;
    }

    private String normalizeToken(String token) {
        if (token == null) return null;
        token = token.trim();
        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }
        return token;
    }
}


