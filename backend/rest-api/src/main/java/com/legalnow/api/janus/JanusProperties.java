package com.legalnow.api.janus;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.janus")
public record JanusProperties(
    String apiUrl,
    String adminSecret
) {}
