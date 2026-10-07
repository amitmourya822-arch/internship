package com.handholding.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    private static final long EXPIRATION_MS = 86400000;

    private static final SecretKey KEY = loadKey();

    private static SecretKey loadKey() {

        String secret = System.getenv("JWT_SECRET");

        if (secret == null || secret.getBytes().length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET environment variable is not set or is shorter than 32 characters. "
                            + "Set JWT_SECRET before running the application.");
        }

        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public static String generateToken(String username, String role) {

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + EXPIRATION_MS)
                )
                .signWith(KEY)
                .compact();
    }

    public static String extractUsername(String token) {

        return parseClaims(token).getSubject();
    }

    public static String extractRole(String token) {

        return parseClaims(token).get("role", String.class);
    }

    public static boolean isTokenValid(String token) {

        try {

            parseClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    private static Claims parseClaims(String token) {

        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}