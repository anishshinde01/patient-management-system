package com.anishshinde.authservice.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component // register as Java Bean
public class JwtUtil {

    // to sign generated JWTs and verify incoming JWTs
    private final Key secretKey;

    /**
     * JWT secret is loaded from external config/env:
     * so the real secret is not committed to the public GitHub repo.
     */
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        // Read Base64-encoded secret and convert it to bytes
        byte[] keyBytes = Base64.getDecoder()
                .decode(secret.getBytes(StandardCharsets.UTF_8));
        // Create HMAC signing key from secret bytes
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Build and sign a JWT containing the user's identity and role.
     */
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email) // identifies user
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(secretKey) // sign token, so tampering can be detected
                .compact(); // Convert JWT into final String form
    }

    public void validateToken(String token) {
        try{
            Jwts.parser().verifyWith((SecretKey) secretKey)
                    .build()
                    .parseSignedClaims(token); // Parse and verify the signed JWT
        } catch (SignatureException e) {
            throw new JwtException("Invalid JWT signature");
        } catch (JwtException e){
            throw new JwtException("Invalid JWT");
        }
    }

}
