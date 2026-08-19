package com.kshrd.blog.util;

import java.time.Instant;

/**
 * Uniform envelope for every successful API response so clients can rely on
 * one shape regardless of endpoint.
 */
public record ApiResponse<T>(boolean success, T data, String message, Instant timestamp) {

    public static <T> ApiResponse<T> of(T data, String message) {
        return new ApiResponse<>(true, data, message, Instant.now());
    }

    public static <T> ApiResponse<T> of(T data) {
        return of(data, null);
    }
}
