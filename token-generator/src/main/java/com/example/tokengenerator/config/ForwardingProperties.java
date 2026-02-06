package com.example.tokengenerator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.forwarding")
public class ForwardingProperties {

    /**
     * Целевой URL, куда отправляется сгенерированный JWT. Если пусто, пересылка отключена.
     */
    private String url;
}
