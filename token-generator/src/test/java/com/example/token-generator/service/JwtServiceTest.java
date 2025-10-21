package com.example.tokengenerator.service;

import com.example.tokengenerator.config.JwtProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void generatesTokenWithExpectedClaims() throws Exception {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("this-is-a-test-secret-key-with-enough-length");
        properties.setIssuer("test-issuer");
        properties.setExpirationSeconds(120);
        properties.setAlgorithm("HC256");
        JwtService service = new JwtService();
        JWTCoder coder = new JWTCoder();
        service.setProperties(properties);
        service.setJwtCoder(coder);

        String token = service.generateToken("alice");

        String[] parts = token.split("\\.");
        assertThat(parts).hasSize(3);

        Base64.Decoder decoder = Base64.getUrlDecoder();
        JsonNode header = objectMapper.readTree(decoder.decode(parts[0]));
        JsonNode payload = objectMapper.readTree(decoder.decode(parts[1]));

        assertThat(header.get("alg").asText()).isEqualTo("HC256");
        assertThat(header.get("typ").asText()).isEqualTo("JWT");

        assertThat(payload.get("sub").asText()).isEqualTo("alice");
        assertThat(payload.get("username").asText()).isEqualTo("alice");
        assertThat(payload.get("iss").asText()).isEqualTo("test-issuer");

        long issuedAt = payload.get("iat").asLong();
        long expiresAt = payload.get("exp").asLong();
        assertThat(Duration.between(Instant.ofEpochSecond(issuedAt), Instant.ofEpochSecond(expiresAt)).getSeconds())
                .isEqualTo(properties.getExpirationSeconds());
        assertThat(Instant.ofEpochSecond(expiresAt)).isAfter(Instant.now().minusSeconds(1));

        String unsigned = parts[0] + "." + parts[1];
        String expectedSignature = coder.signWithAlg(unsigned, properties.getSecret(), properties.getAlgorithm());
        assertThat(parts[2]).isEqualTo(expectedSignature);
    }
}
