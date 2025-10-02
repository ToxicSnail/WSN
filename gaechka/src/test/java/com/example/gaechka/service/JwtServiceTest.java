package com.example.gaechka.service;

import com.example.gaechka.config.JwtProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
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
        JwtService service = new JwtService();
        service.setProperties(properties);
        service.setJwtCoder(new JWTCoder());

        String token = service.generateToken("alice");

        String[] parts = token.split("\.");
        assertThat(parts).hasSize(3);

        Base64.Decoder decoder = Base64.getUrlDecoder();
        JsonNode header = objectMapper.readTree(decoder.decode(parts[0]));
        JsonNode payload = objectMapper.readTree(decoder.decode(parts[1]));

        assertThat(header.get("alg").asText()).isEqualTo("HS256");
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
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(properties.getSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] expectedSignature = mac.doFinal(unsigned.getBytes(StandardCharsets.UTF_8));
        String encodedSignature = Base64.getUrlEncoder().withoutPadding().encodeToString(expectedSignature);
        assertThat(parts[2]).isEqualTo(encodedSignature);
    }
}
