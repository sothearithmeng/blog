package com.kshrd.blog.dto.response;

import java.util.List;
import org.springframework.data.domain.Page;

/** Pagination envelope decoupled from Spring's Page so it stays JSON-stable across versions. */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
