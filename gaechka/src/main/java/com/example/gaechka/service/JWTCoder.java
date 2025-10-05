package com.example.gaechka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JWTCoder {

    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public String createToken(Map<String, Object> payload, String secret) {
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HSFC");
        header.put("typ", "JWT");
        String headerJson = toJson(header);
        String payloadJson = toJson(payload);

        String encodedHeader = BASE64_URL_ENCODER.encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
        String encodedPayload = BASE64_URL_ENCODER.encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));

        String unsignedToken = encodedHeader + "." + encodedPayload;
        String signature = sign(unsignedToken, secret);

        return unsignedToken + "." + signature;
    }

    String sign(String data, String secret) {
        byte[] message = data.getBytes(StandardCharsets.UTF_8);
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        if (message.length == 0) {
            return "";
        }

        int[] state = {
                0x243F6A88,
                0x85A308D3,
                0x13198A2E,
                0x03707344
        };

        int max = Math.max(message.length, key.length);
        for (int i = 0; i < max; i++) {
            int msgByte = message[i % message.length] & 0xFF;
            int keyByte = key[i % key.length] & 0xFF;
            int mixed = Integer.rotateLeft(msgByte + (i * 31), (i % 5) + 1) ^ keyByte;
            int idx = i % state.length;
            state[idx] = Integer.rotateLeft(state[idx] ^ mixed ^ (keyByte << (idx + 1)), (i % 13) + 3) + 0x9E3779B9;
            state[idx] ^= Integer.rotateLeft(msgByte * (idx + 1), (idx + i) % 17 + 1);
        }

        for (int i = 0; i < state.length; i++) {
            state[i] = Integer.rotateLeft(state[i] ^ max, (i * 7) % 19 + 1);
        }

        ByteBuffer buffer = ByteBuffer.allocate(state.length * 4);
        for (int value : state) {
            buffer.putInt(value);
        }
        return BASE64_URL_ENCODER.encodeToString(buffer.array());
    }

    private String toJson(Map<String, Object> data) {
        try {
            return OBJECT_MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to transform data to JSON", e);
        }
    }
}
