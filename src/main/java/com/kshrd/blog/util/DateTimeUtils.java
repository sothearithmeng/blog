package com.kshrd.blog.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/** Common date/time conversions kept consistent across the codebase (UTC by default). */
public final class DateTimeUtils {

    public static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private DateTimeUtils() {}

    public static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.toInstant(ZoneOffset.UTC);
    }

    public static LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    public static LocalDateTime toLocalDateTime(Instant instant, ZoneId zoneId) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, zoneId);
    }

    public static String format(LocalDate date) {
        return date == null ? null : date.format(ISO_DATE);
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(ISO_DATE_TIME);
    }

    public static boolean isBetween(LocalDateTime value, LocalDateTime start, LocalDateTime end) {
        return value != null
                && start != null
                && end != null
                && !value.isBefore(start)
                && !value.isAfter(end);
    }
}
