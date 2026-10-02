package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for handling dates using the modern Java Time API (LocalDate).
 */
public class DateUtil {

    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    public static final String DISPLAY_DATE_PATTERN = "dd MMM yyyy";

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN);
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern(DISPLAY_DATE_PATTERN);

    private DateUtil() {
        // Prevent instantiation
    }

    /**
     * Formats a LocalDate into standard ISO-8601 string (yyyy-MM-dd).
     */
    public static String formatIso(LocalDate date) {
        if (date == null) return "";
        return date.format(ISO_FORMATTER);
    }

    /**
     * Formats a LocalDate into human-readable format (dd MMM yyyy).
     */
    public static String formatDisplay(LocalDate date) {
        if (date == null) return "N/A";
        return date.format(DISPLAY_FORMATTER);
    }

    /**
     * Parses an ISO date string (yyyy-MM-dd) into LocalDate safely.
     * Returns null if parsing fails.
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr.trim(), ISO_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Verifies if a string is a valid ISO date.
     */
    public static boolean isValidIsoDate(String dateStr) {
        return parseDate(dateStr) != null;
    }

    /**
     * Calculates the number of days remaining until expiry.
     * Returns negative values if already expired.
     */
    public static long daysUntilExpiry(LocalDate expiryDate) {
        if (expiryDate == null) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    /**
     * Validates that expiry date is strictly after manufacturing date.
     */
    public static boolean isExpiryValid(LocalDate mfgDate, LocalDate expDate) {
        if (mfgDate == null || expDate == null) {
            return false;
        }
        return expDate.isAfter(mfgDate);
    }
}
