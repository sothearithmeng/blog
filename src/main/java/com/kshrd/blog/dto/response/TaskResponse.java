package com.kshrd.blog.dto.response;

import com.kshrd.blog.entity.Task;
import com.kshrd.blog.util.DateTimeUtils;
import com.kshrd.blog.util.StringUtils;

/** Response shape for a {@link Task}, decoupled from the entity so the API stays stable. */
public record TaskResponse(
        String id,
        String title,
        String description,
        boolean completed,
        String reporterEmail,
        String createdAt,
        String updatedAt) {

    private static final int DESCRIPTION_PREVIEW_LENGTH = 280;
    private static final int EMAIL_VISIBLE_CHARS = 3;

    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId().toString(),
                task.getTitle(),
                StringUtils.truncate(task.getDescription(), DESCRIPTION_PREVIEW_LENGTH),
                task.isCompleted(),
                StringUtils.mask(task.getReporterEmail(), EMAIL_VISIBLE_CHARS),
                DateTimeUtils.format(DateTimeUtils.toLocalDateTime(task.getCreatedAt())),
                DateTimeUtils.format(DateTimeUtils.toLocalDateTime(task.getUpdatedAt())));
    }
}
