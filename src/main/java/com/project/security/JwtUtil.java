// Requirements:
// - Generate a random HMAC-SHA256 signing key at startup
// - Provide generateToken(username, role) returning a signed JWT with 24h expiry
// - Provide parseToken(token) returning Claims or null on failure

package com.project.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    // Random key generated once per app lifecycle
    private static final SecretKey KEY = Jwts.SIG.HS256.key().build();
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24 hours

    private JwtUtil() {
    }

    /**
     * Create a signed JWT containing username and role claims.
     */
    public static String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(KEY)
                .compact();
    }

    /**
     * Parse and validate a JWT. Returns Claims on success, null on failure.
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("[JwtUtil] Invalid token: " + e.getMessage());
            return null;
        }
    }
}
