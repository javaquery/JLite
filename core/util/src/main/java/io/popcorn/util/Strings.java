package io.popcorn.util;

import io.popcorn.util.collection.Collections;
import java.text.Normalizer;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public final class Strings {
    public static final String EMPTY_STRING = "";
    private static final String UNSUPPORTED_ASCII_PATTERN = "[^\\x20-\\x7e]";
    private static final String UNSUPPORTED_UNICODE_PATTERN = "[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+";

    private Strings() {}

    /**
     * Returns {@code true} if the provided String is {@code null} or empty otherwise returns {@code
     * false}.
     *
     * @param str a String to be checked against {@code null} or empty
     * @return {@code true} if the provided String is {@code null} or empty otherwise {@code false}
     */
    public static boolean nullOrEmpty(String str) {
        return Objects.isNull(str) || str.trim().isEmpty();
    }

    /**
     * Execute code if the provided String is {@code null} or empty.
     * @param str a String to be checked against {@code null} or empty
     * @param executableFunction lambda function given executed if the provided String is {@code null} or empty.
     */
    public static void nullOrEmpty(String str, ExecutableFunction executableFunction) {
        if (nullOrEmpty(str)) {
            executableFunction.execute();
        }
    }

    /**
     * Checks if the provided String is {@code null} or empty.
     * @param str a String to be checked against {@code null} or empty
     * @param defaultValue - if the provided String is {@code null} or empty then this value will be returned.
     * @return provided String if non-{@code null} and non-empty otherwise defaultValue
     */
    public static String nullOrEmpty(String str, String defaultValue) {
        return nullOrEmpty(str) ? defaultValue : str;
    }

    /**
     * Returns {@code true} if the provided String is non-{@code null} and non-empty otherwise returns
     * {@code false}.
     *
     * @param str a String to be checked against non-{@code null} and non-empty
     * @return {@code true} if the provided String is non-{@code null} and non-empty otherwise {@code
     *     false}
     */
    public static boolean nonNullNonEmpty(String str) {
        return Objects.nonNull(str) && !str.trim().isEmpty();
    }

    /**
     * Execute code if the provided String is non-{@code null} and non-empty.
     * @param str a String to be checked against non-{@code null} and non-empty
     * @param executableFunction lambda function given executed if the provided String is non-{@code null} and non-empty.
     */
    public static void nonNullNonEmpty(String str, ExecutableFunction executableFunction) {
        if (nonNullNonEmpty(str)) {
            executableFunction.execute();
        }
    }

    /**
     * Checks if the provided String is non-{@code null} and non-empty.
     * @param str a String to be checked against non-{@code null} and non-empty
     * @param defaultValue - if the provided String is non-{@code null} and non-empty then this value will be returned.
     * @return provided String if non-{@code null} and non-empty otherwise defaultValue
     */
    public static String nonNullNonEmpty(String str, String defaultValue) {
        return nonNullNonEmpty(str) ? str : defaultValue;
    }

    /**
     * Returns trimmed
     *
     * @param str a String to trim
     * @return trimmed String if provided String is non-{@code null} otherwise null
     */
    public static String trimString(String str) {
        return Objects.nonNull(str) ? str.trim() : null;
    }

    /**
     * Returns joined string with provided separator
     *
     * @param separator a String used for joining multiple Strings
     * @param strings array of String to join
     * @return joined String
     */
    public static String joinStrings(String separator, String... strings) {
        if (Objects.nonNull(separator)) {
            StringJoiner stringJoiner = new StringJoiner(separator);
            if (Objects.nonNull(strings)) {
                for (String str : strings) {
                    if (nonNullNonEmpty(str)) {
                        stringJoiner.add(str);
                    }
                }
            }
            return stringJoiner.toString();
        }
        return null;
    }

    /**
     * Returns joined string with provided separator
     *
     * @param separator a String used for joining multiple Strings
     * @param strings array of String to join
     * @return joined String
     */
    public static String joinStringList(String separator, List<String> strings) {
        if (Objects.nonNull(separator)) {
            StringJoiner stringJoiner = new StringJoiner(separator);
            if (Collections.nonNullNonEmpty(strings)) {
                for (String str : strings) {
                    if (nonNullNonEmpty(str)) {
                        stringJoiner.add(str);
                    }
                }
            }
            return stringJoiner.toString();
        }
        return null;
    }

    /**
     * Returns {@code true} if the provided String is equalsIgnoreCase to [true, yes, y, 1] otherwise
     * returns {@code false}.
     *
     * @param str a String to be checked against equalsIgnoreCase [true, yes, y, 1]
     * @return {@code true} if the provided String is equalsIgnoreCase to [true, yes, y, 1] otherwise
     *     {@code false}
     */
    public static boolean optBooleanFromString(String str) {
        return "true".equalsIgnoreCase(str)
                || "yes".equalsIgnoreCase(str)
                || "y".equalsIgnoreCase(str)
                || "1".equalsIgnoreCase(str);
    }

    /**
     * Returns String after removing not supported ASCII characters
     *
     * @param str a String to be checked for non ASCII Characters
     * @return Returns String after removing non ASCII characters
     *     <p>reference:
     *     https://stackoverflow.com/questions/10574289/remove-non-ascii-characters-from-string-in-java
     */
    public static String removeNotSupportedASCIICharacters(String str) {
        if (nonNullNonEmpty(str)) {
            return str.replaceAll(UNSUPPORTED_ASCII_PATTERN, "");
        }
        return null;
    }

    /**
     * Returns String after removing not supported Unicode Characters
     *
     * @param str a String to be checked for not supported Unicode Characters
     * @return Returns String after removing not supported Unicode Characters
     *     <p>reference:
     *     https://stackoverflow.com/questions/24840667/what-is-the-regex-to-extract-all-the-emojis-from-a-string
     */
    public static String removeNotSupportedUnicodeCharacters(String str) {
        if (nonNullNonEmpty(str)) {
            return str.replaceAll(UNSUPPORTED_UNICODE_PATTERN, "");
        }
        return null;
    }

    /**
     * Normalize a sequence of char values. The sequence will be normalized according to the specified normalization from.
     *
     * @param str The sequence of char values to normalize.
     * @return The normalized String
     * @throws NullPointerException If src or form is null.
     */
    public static String normalize(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD);
    }

    /**
     * Check String contains any diacritics or not.
     * @param str a String to be checked for diacritics Characters
     * @return {@code true} if the provided String contains diacritics otherwise
     *     {@code false}
     */
    public static boolean hasDiacritics(String str) {
        String str2 = normalize(str);
        return str2.matches("(?s).*\\p{InCombiningDiacriticalMarks}.*");
    }

    /**
     * Compares this string to the specified string. The result is true if and only if the argument is not null and
     * is a String object that represents the same sequence of characters as this object.
     * @param a String
     * @param b String to be compared with {@code a} for equality
     * @param executableFunction lambda function given executed if the provided Strings are equals.
     */
    public static void equals(String a, String b, ExecutableFunction executableFunction) {
        if (Objects.equals(a, b)) {
            executableFunction.execute();
        }
    }

    /**
     * Compares this string to the specified string. The result is true if and only if the one argument is null or
     * a String object that represents the different sequence of characters as this object.
     * @param a String
     * @param b String to be compared with {@code a} for non equality
     * @param executableFunction lambda function given executed if the provided Strings are not equals.
     */
    public static void notEquals(String a, String b, ExecutableFunction executableFunction) {
        if (!Objects.equals(a, b)) {
            executableFunction.execute();
        }
    }

    /**
     * Compares this String to another String, ignoring case considerations. Two strings are considered equal ignoring case if they are of the same length and corresponding characters in the two strings are equal ignoring case.
     * Two characters c1 and c2 are considered the same ignoring case if at least one of the following is true:
     * <ul>
     *     <li>The two characters are the same (as compared by the == operator)</li>
     *     <li>Calling Character.toLowerCase(Character.toUpperCase(char)) on each character produces the same result</li>
     * </ul>
     *
     * @param a String
     * @param b String to be compared with {@code a} for equality (ignoring case considerations)
     * @return {@code true} if the provided Strings are equalsIgnoreCase otherwise
     *    *     {@code false}
     */
    public static boolean equalsIgnoreCase(String a, String b) {
        return (a == null && b == null) || (a != null && a.equalsIgnoreCase(b));
    }

    /**
     * Compares this String to another String, ignoring case considerations. Two strings are considered equal ignoring case if they are of the same length and corresponding characters in the two strings are equal ignoring case.
     * Two characters c1 and c2 are considered the same ignoring case if at least one of the following is true:
     * <ul>
     *     <li>The two characters are the same (as compared by the == operator)</li>
     *     <li>Calling Character.toLowerCase(Character.toUpperCase(char)) on each character produces the same result</li>
     * </ul>
     *
     * @param a String
     * @param b String to be compared with {@code a} for equality (ignoring case considerations)
     * @param executableFunction lambda function given executed if the provided Strings are equals (ignoring case considerations).
     */
    public static void equalsIgnoreCase(String a, String b, ExecutableFunction executableFunction) {
        if (equalsIgnoreCase(a, b)) {
            executableFunction.execute();
        }
    }

    /**
     * Compares this String to another String, ignoring case considerations. Two strings are considered not equal ignoring case if they are of the not same length and corresponding characters in the two strings are not equal ignoring case.
     *
     * @param a String
     * @param b String to be compared with {@code a} for non equality (ignoring case considerations)
     * @param executableFunction lambda function given executed if the provided Strings are non equals (ignoring case considerations).
     */
    public static void notEqualsIgnoreCase(String a, String b, ExecutableFunction executableFunction) {
        if (!equalsIgnoreCase(a, b)) {
            executableFunction.execute();
        }
    }
}
