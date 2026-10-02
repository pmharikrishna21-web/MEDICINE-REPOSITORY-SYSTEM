package util;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Validation utility providing robust input validation routines
 * for registration, login, and medicine CRUD operations.
 */
public class ValidationUtil {

    // RFC 5322 compliant simplified email regex
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );

    // Standard phone regex: allows digits, dashes, spaces, plus sign, parentheses (7 to 20 chars)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[0-9\\s\\-\\(\\)]{7,20}$"
    );

    // Alphanumeric username (3 to 30 characters)
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_]{3,30}$"
    );

    private ValidationUtil() {
        // Prevent instantiation
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidUsername(String username) {
        if (isEmpty(username)) return false;
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    public static boolean isValidPrice(String priceStr) {
        if (isEmpty(priceStr)) return false;
        try {
            double price = Double.parseDouble(priceStr.trim());
            return price >= 0.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidStock(String stockStr) {
        if (isEmpty(stockStr)) return false;
        try {
            int stock = Integer.parseInt(stockStr.trim());
            return stock >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean areDatesValid(LocalDate mfgDate, LocalDate expDate) {
        if (mfgDate == null || expDate == null) {
            return false;
        }
        return expDate.isAfter(mfgDate);
    }
}
