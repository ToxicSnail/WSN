package com.example.gaechka.service;

import com.example.gaechka.config.JwtProperties;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final int MIN_SECRET_LENGTH = 32;

    @Setter(onMethod_ = @Autowired)
    private JwtProperties properties;

    @Setter(onMethod_ = @Autowired)
    private JWTCoder jwtCoder;

    public String generateToken(String username) {
        String secret = properties.getSecret();
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_LENGTH) {
            throw new IllegalStateException("JWT secret must be at least " + MIN_SECRET_LENGTH + " bytes");
        }

        Date issuedAt = new Date();
        long expirationMillis = issuedAt.getTime() + properties.getExpirationSeconds() * 1000;
        Date expiration = new Date(expirationMillis);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", username);
        payload.put("username", username);
        payload.put("iss", properties.getIssuer());
        payload.put("iat", issuedAt.getTime() / 1000);
        payload.put("exp", expiration.getTime() / 1000);

        String alg = properties.getAlgorithm();
        return jwtCoder.createToken(payload, secret, alg);
    }
}
