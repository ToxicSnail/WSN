package com.example.gaechka.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String secret;
    private String algorithm = "HC256"; // weak
    private long expirationSeconds = 3600;
    private String issuer = "auth-service";
}
