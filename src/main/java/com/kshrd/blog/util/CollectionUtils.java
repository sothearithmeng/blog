package com.kshrd.blog.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Null-safe collection helpers to avoid repeated null checks before iteration. */
public final class CollectionUtils {

    private CollectionUtils() {}

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static <T> List<T> emptyIfNull(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    /** Splits a list into fixed-size chunks, e.g. for batching bulk inserts or API calls. */
    public static <T> List<List<T>> partition(List<T> list, int size) {
        if (size < 1) {
            throw new IllegalArgumentException("size must be at least 1");
        }
        if (isEmpty(list)) {
            return Collections.emptyList();
        }
        List<List<T>> chunks = new java.util.ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            chunks.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return chunks;
    }
}
