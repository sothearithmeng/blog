package com.kshrd.blog.util;

import java.util.regex.Pattern;

/** Lightweight format checks for cases simpler than a full Bean Validation annotation. */
public final class ValidationUtils {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern UUID_PATTERN =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    private ValidationUtils() {}

    public static boolean isValidEmail(String value) {
        return value != null && EMAIL_PATTERN.matcher(value).matches();
    }

    public static boolean isValidUuid(String value) {
        return value != null && UUID_PATTERN.matcher(value).matches();
    }

    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
}
