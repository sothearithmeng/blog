package com.kshrd.blog.util;

/** Null-safe string helpers not already covered by {@code java.lang.String}. */
public final class StringUtils {

    private StringUtils() {}

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    public static String defaultIfBlank(String value, String defaultValue) {
        return isBlank(value) ? defaultValue : value;
    }

    public static String truncate(String value, int maxLength) {
        if (maxLength < 0) {
            throw new IllegalArgumentException("maxLength must not be negative");
        }
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    public static String capitalize(String value) {
        if (isBlank(value)) {
            return value;
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    /** Masks all but the last {@code visibleChars} characters, e.g. for logging PII. */
    public static String mask(String value, int visibleChars) {
        if (visibleChars < 0) {
            throw new IllegalArgumentException("visibleChars must not be negative");
        }
        if (value == null) {
            return null;
        }
        if (value.length() <= visibleChars) {
            return value;
        }
        if (isBlank(value)) {
            return "*".repeat(value.length());
        }
        int maskedLength = value.length() - visibleChars;
        return "*".repeat(maskedLength) + value.substring(maskedLength);
    }
}
