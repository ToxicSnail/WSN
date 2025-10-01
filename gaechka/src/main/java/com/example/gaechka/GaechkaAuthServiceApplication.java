package com.example.gaechka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GaechkaAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GaechkaAuthServiceApplication.class, args);
    }
}
