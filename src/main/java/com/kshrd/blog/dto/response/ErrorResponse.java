package com.kshrd.blog.dto.response;

import java.time.Instant;
import java.util.List;

/** Uniform error envelope returned by {@code GlobalExceptionHandler}. */
public record ErrorResponse(
        String code,
        String message,
        int status,
        Instant timestamp,
        String path,
        List<FieldError> fieldErrors) {

    public record FieldError(String field, String message) {}
}
