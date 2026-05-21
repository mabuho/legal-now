package com.legalnow.api.sep;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.sep")
public record SepCedulaProperties(
    String apiBase,
    String apiKey,
    String clientId
) {}
