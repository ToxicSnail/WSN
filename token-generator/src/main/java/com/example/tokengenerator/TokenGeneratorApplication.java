package com.example.tokengenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TokenGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(TokenGeneratorApplication.class, args);
    }
}
