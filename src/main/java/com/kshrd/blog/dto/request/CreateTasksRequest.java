package com.kshrd.blog.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** Wraps a batch of {@link CreateTaskRequest} so Bean Validation cascades into each element. */
public record CreateTasksRequest(
        @NotEmpty(message = "tasks must contain at least one item") List<@Valid CreateTaskRequest> tasks) {
}
