package com.example.gaechka.service;

import com.example.gaechka.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    @Test
    void generatesTokenWithExpectedClaims() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("this-is-a-test-secret-key-with-enough-length-pupupu");
        properties.setIssuer("test-issuer");
        properties.setExpirationSeconds(120);
        JwtService service = new JwtService();
        service.setProperties(properties);

        String token = service.generateToken("alice");

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertThat(claims.getSubject()).isEqualTo("alice");
        assertThat(claims.getIssuer()).isEqualTo("test-issuer");
        assertThat(Duration.between(claims.getIssuedAt().toInstant(), claims.getExpiration().toInstant()).getSeconds())
                .isEqualTo(properties.getExpirationSeconds());
        assertThat(claims.get("username", String.class)).isEqualTo("alice");
        assertThat(claims.getExpiration().toInstant()).isAfter(Instant.now());
    }
}
