package com.kshrd.blog.dto.request;

import jakarta.validation.constraints.Size;

/** Every field is optional so callers can send a partial (PATCH-style) update. */
public record UpdateTaskRequest(
        @Size(max = 200, message = "title must be at most 200 characters") String title,
        @Size(max = 2000, message = "description must be at most 2000 characters") String description,
        Boolean completed) {
}
