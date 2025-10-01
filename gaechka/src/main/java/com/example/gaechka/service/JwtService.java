package com.example.gaechka.service;

import com.example.gaechka.config.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final int MIN_SECRET_LENGTH = 32; // HS256 требует минимум 256 бит секрета

    private JwtProperties properties;

    @Autowired
    public void setProperties(JwtProperties properties) {
        this.properties = properties;
    }

    public String generateToken(String username) {
        String secret = properties.getSecret();
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_LENGTH) {
            throw new IllegalStateException("JWT secret must be at least " + MIN_SECRET_LENGTH + " bytes");
        }

        Date issuedAt = new Date();
        long expirationMillis = issuedAt.getTime() + properties.getExpirationSeconds() * 1000;
        Date expiration = new Date(expirationMillis);

        return Jwts.builder()
                .setSubject(username)
                .setIssuer(properties.getIssuer())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .claim("username", username)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }
}
