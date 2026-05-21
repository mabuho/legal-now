package com.legalnow.api.sep;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.sep")
public record SepCedulaProperties(
    String apiBase,
    String apiPath,
    String apiAuth,
    String apiKey,
    String clientId,
    String host,
    String refer
) {}
