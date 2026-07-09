package io.stageclear.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

public final class JwtUtil {

    private JwtUtil() {
    }

    public static String generateToken(
            String secret,
            Long expireSeconds,
            String subject,
            Map<String, Object> claims
    ) {
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + expireSeconds * 1000);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expireAt)
                .signWith(getSigningKey(secret))
                .compact();
    }

    public static Claims parseToken(String secret, String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static boolean validateToken(String secret, String token) {
        try {
            parseToken(secret, token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isExpired(String secret, String token) {
        Claims claims = parseToken(secret, token);
        return claims.getExpiration().before(new Date());
    }

    private static SecretKey getSigningKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}