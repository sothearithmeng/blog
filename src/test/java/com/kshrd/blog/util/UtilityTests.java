package com.kshrd.blog.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class UtilityTests {

    @Test
    void partitionRejectsZeroSize() {
        assertThrows(IllegalArgumentException.class, () -> CollectionUtils.partition(List.of("item"), 0));
    }

    @Test
    void maskKeepsValuesWhenEverythingIsVisible() {
        assertEquals("abc", StringUtils.mask("abc", 3));
        assertThrows(IllegalArgumentException.class, () -> StringUtils.mask("abc", -1));
    }

    @Test
    void betweenReturnsFalseWhenAnyValueIsMissing() {
        assertFalse(DateTimeUtils.isBetween(LocalDateTime.now(), null, LocalDateTime.now()));
    }
}
