package com.example.tokengenerator.service;

import com.example.tokengenerator.config.ForwardingProperties;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TokenForwarder {

    private static final Logger log = LoggerFactory.getLogger(TokenForwarder.class);

    @Autowired
    private ForwardingProperties properties;

    private final RestTemplate restTemplate = new RestTemplate();

    public void forward(String token) {
        if (properties == null || !StringUtils.hasText(properties.getUrl())) {
            log.debug("Forwarding skipped, URL was not set");
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("token", token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(properties.getUrl(), httpEntity, Void.class);
            log.info("JWT sent to {}", properties.getUrl());
        } catch (RestClientException ex) {
            log.error("Can't send JWT to {}", properties.getUrl(), ex);
            throw ex;
        }
    }
}
