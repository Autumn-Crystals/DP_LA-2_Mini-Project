package com.campus.complaints.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility for formatting and parsing dates and timestamps across tables and views.
 */
public final class DateTimeFormatterUtil {

    private DateTimeFormatterUtil() {}

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private static final DateTimeFormatter SHORT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) return "—";
        return dateTime.format(DISPLAY_FORMAT);
    }

    public static String formatShort(LocalDateTime dateTime) {
        if (dateTime == null) return "—";
        return dateTime.format(SHORT_DATE_FORMAT);
    }
}
