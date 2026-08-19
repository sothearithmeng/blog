package com.kshrd.blog.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds {@code app.cors.*} so allowed origins can differ per environment without code changes.
 * {@code allowedOrigins} has no wildcard default on purpose: each environment (see
 * {@code application-dev.yml} / {@code application-prod.yml}) must opt in to specific origins.
 * An empty list means "no origins configured" and {@link CorsConfig} disables CORS entirely.
 */
@ConfigurationProperties(prefix = "app.cors")
public record CorsProperties(
        List<String> allowedOrigins,
        List<String> allowedMethods,
        List<String> allowedHeaders,
        boolean allowCredentials) {

    public CorsProperties {
        allowedOrigins = allowedOrigins == null ? List.of() : allowedOrigins;
        if (allowedMethods == null || allowedMethods.isEmpty()) {
            allowedMethods = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
        }
        if (allowedHeaders == null || allowedHeaders.isEmpty()) {
            allowedHeaders = List.of("*");
        }
    }
}
