package com.example.gaechka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.forwarding")
public class ForwardingProperties {

    //
    // Целевой URL, куда отправляется сгенерированный JWT. Если пусто, пересылка отключена.
    //
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
