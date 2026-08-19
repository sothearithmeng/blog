package com.kshrd.blog.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/** Turns raw page/size/sort query params into a sane, bounded {@link Pageable}. */
public final class PageUtils {

    private static final int MAX_PAGE_SIZE = 100;

    private PageUtils() {}

    public static Pageable of(int page, int size, String sortBy, String direction) {
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int safePage = Math.max(page, 0);
        if (StringUtils.isBlank(sortBy)) {
            return PageRequest.of(safePage, safeSize);
        }
        Sort sort = Sort.by(Sort.Direction.fromOptionalString(direction).orElse(Sort.Direction.ASC), sortBy);
        return PageRequest.of(safePage, safeSize, sort);
    }
}
