package com.kshrd.blog.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Applies {@link CorsProperties} to every {@code /api/**} endpoint. */
@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class CorsConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    public CorsConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (corsProperties.allowedOrigins().isEmpty()) {
            // No origins configured: leave CORS unregistered so cross-origin requests are
            // rejected by the browser instead of silently defaulting to "allow everything".
            return;
        }
        registry.addMapping("/api/**")
                .allowedOrigins(corsProperties.allowedOrigins().toArray(new String[0]))
                .allowedMethods(corsProperties.allowedMethods().toArray(new String[0]))
                .allowedHeaders(corsProperties.allowedHeaders().toArray(new String[0]))
                .allowCredentials(corsProperties.allowCredentials());
    }
}
