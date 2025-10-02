package com.example.gaechka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;


// Простейший самописный кодер для выпуска JWT токенов (HMAC-SHA256).

@Component
public class JWTCoder {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public String createToken(Map<String, Object> payload, String secret) {
        String headerJson = toJson(Map.of("alg", "HS256", "typ", "JWT"));
        String payloadJson = toJson(payload);

        String encodedHeader = BASE64_URL_ENCODER.encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
        String encodedPayload = BASE64_URL_ENCODER.encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));

        String unsignedToken = encodedHeader + "." + encodedPayload;
        String signature = sign(unsignedToken, secret);

        return unsignedToken + "." + signature;
    }

    private String sign(String data, String secret) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(key);
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return BASE64_URL_ENCODER.encodeToString(raw);
        } catch (Exception ex) {
            throw new IllegalStateException("Не удалось подписать JWT", ex);
        }
    }

    private String toJson(Map<String, Object> data) {
        try {
            return OBJECT_MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Не удалось преобразовать данные в JSON", e);
        }
    }
}
