package com.kshrd.blog.util;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Standalone Jackson helper for quick serialize/deserialize outside the request pipeline
 * (logging, caching, message payloads). Controllers should keep using the injected
 * {@link ObjectMapper} bean instead of this one. Built on Jackson 3 ({@code tools.jackson.*}),
 * the default in Spring Boot 4.
 */
public final class JsonUtils {

    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    private JsonUtils() {}

    public static String toJson(Object value) {
        return value == null ? null : MAPPER.writeValueAsString(value);
    }

    public static <T> T fromJson(String json, Class<T> type) {
        return StringUtils.isBlank(json) ? null : MAPPER.readValue(json, type);
    }
}
