package com.pm.auth_service.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    private final Key secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret){
        byte[] keyBytes = Base64.getDecoder()
                .decode(secret.getBytes(StandardCharsets.UTF_8));
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email, String role){
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5))
                .signWith(secretKey)
                .compact();
    }

    public void validateToken(String token) {
        try{
            log.info("**************** SecretKey = {}", secretKey);
            Jwts.parser().verifyWith((SecretKey) secretKey)
                    .build()
                    .parseSignedClaims(token);
        } catch(SignatureException e){
            log.info("**************** Invalid JWT signature");
            throw new JwtException("Invalid JWT signature");
        } catch (JwtException e) {
            log.info("**************** Invalid JWT");
            throw new JwtException("Invalid JWT");
        }
    }
}
