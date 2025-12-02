package io.popcorn.util.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public final class LocalDates {

    private LocalDates() {}

    /**
     * Adds or subtracts the specified amount of time to the given LocalDate. For example, to
     * subtract 5 days from the given {@code LocalDate} you can achieve it by calling:
     *
     * <p><code>addInLocalDate(LocalDate.now(), Calendar.DAY_OF_MONTH, -5)</code>.
     *
     * @param date a {@code LocalDate} to Adds or subtracts the specified amount of time
     * @param type the calendar field. For example, {@code Calendar.DAY_OF_MONTH}
     * @param amount the amount of date or time to be added to the field.
     * @return Returns {@code LocalDate} with addition or subtraction
     */
    public static LocalDate addInLocalDate(LocalDate date, int type, int amount) {
        if (date != null) {
            switch (type) {
                case Calendar.DAY_OF_MONTH:
                    return date.plusDays(amount);
                case Calendar.MONTH:
                    return date.plusMonths(amount);
                case Calendar.YEAR:
                    return date.plusYears(amount);
                default:
                    throw new IllegalArgumentException("Unsupported type: " + type);
            }
        }
        return null;
    }

    /**
     * Adds or subtracts the specified amount of time to the given LocalDateTime. For example, to
     * subtract 5 days from the given {@code LocalDateTime} you can achieve it by calling:
     *
     * <p><code>addInLocalDateTime(LocalDateTime.now(), Calendar.DAY_OF_MONTH, -5)</code>.
     *
     * @param dateTime a {@code LocalDateTime} to Adds or subtracts the specified amount of time
     * @param type the calendar field. For example, {@code Calendar.DAY_OF_MONTH}
     * @param amount the amount of date or time to be added to the field.
     * @return Returns {@code LocalDateTime} with addition or subtraction
     */
    public static LocalDateTime addInLocalDateTime(LocalDateTime dateTime, int type, int amount) {
        if (dateTime != null) {
            switch (type) {
                case Calendar.DAY_OF_MONTH:
                    return dateTime.plusDays(amount);
                case Calendar.MONTH:
                    return dateTime.plusMonths(amount);
                case Calendar.YEAR:
                    return dateTime.plusYears(amount);
                case Calendar.WEEK_OF_YEAR:
                    return dateTime.plusWeeks(amount);
                case Calendar.HOUR:
                case Calendar.HOUR_OF_DAY:
                    return dateTime.plusHours(amount);
                case Calendar.MINUTE:
                    return dateTime.plusMinutes(amount);
                case Calendar.SECOND:
                    return dateTime.plusSeconds(amount);
                default:
                    throw new IllegalArgumentException("Unsupported type: " + type);
            }
        }
        return null;
    }

    /**
     * Adds or subtracts the specified amount of time to current {@code LocalDate}. For example, to
     * subtract 5 days from current {@code LocalDate} you can achieve it by calling:
     *
     * <p><code>addInCurrentLocalDate(Calendar.DAY_OF_MONTH, -5)</code>.
     *
     * @param type the calendar field. For example, {@code Calendar.DAY_OF_MONTH}
     * @param amount the amount of date or time to be added to the field.
     * @return Returns current {@code LocalDate} with addition or subtraction
     */
    public static LocalDate addInCurrentLocalDate(int type, int amount) {
        return addInLocalDate(LocalDate.now(), type, amount);
    }

    /**
     * Adds or subtracts the specified amount of time to current {@code LocalDateTime}. For example,
     * to subtract 5 days from current {@code LocalDateTime} you can achieve it by calling:
     *
     * <p><code>addInCurrentLocalDateTime(Calendar.DAY_OF_MONTH, -5)</code>.
     *
     * @param type the calendar field. For example, {@code Calendar.DAY_OF_MONTH}
     * @param amount the amount of date or time to be added to the field.
     * @return Returns current {@code LocalDateTime} with addition or subtraction
     */
    public static LocalDateTime addInCurrentLocalDateTime(int type, int amount) {
        return addInLocalDateTime(LocalDateTime.now(), type, amount);
    }

    /**
     * Parses the given date string into a LocalDate using the specified DateTimeFormat and ZoneId.
     *
     * @param date the date string to parse
     * @param dateTimeFormat the DateTimeFormat to use for parsing
     * @param zoneId the ZoneId to consider during parsing
     * @return the parsed LocalDate, or null if parsing fails
     */
    public static LocalDate parseLocalDate(String date, DateTimeFormat dateTimeFormat, ZoneId zoneId) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat.getValue());

            // Try to parse as ZonedDateTime first (for patterns with timezone info)
            try {
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(date, formatter.withZone(zoneId));
                return zonedDateTime.toLocalDate();
            } catch (Exception e) {
                // If that fails, try parsing as LocalDateTime directly
                return LocalDate.parse(date, formatter);
            }
        } catch (Exception e) {
            /* Silent exception - matches your original behavior */
        }
        return null;
    }

    /**
     * Parses the given date string into a LocalDate using the specified DateTimeFormat and the system default ZoneId.
     *
     * @param date the date string to parse
     * @param dateTimeFormat the DateTimeFormat to use for parsing
     * @return the parsed LocalDate, or null if parsing fails
     */
    public static LocalDate parseLocalDate(String date, DateTimeFormat dateTimeFormat) {
        return parseLocalDate(date, dateTimeFormat, ZoneId.systemDefault());
    }

    /**
     * Parses the given date string into a LocalDateTime using the specified DateTimeFormat and ZoneId.
     *
     * @param date the date string to parse
     * @param dateTimeFormat the DateTimeFormat to use for parsing
     * @param zoneId the ZoneId to consider during parsing
     * @return the parsed LocalDateTime, or null if parsing fails
     */
    public static LocalDateTime parseLocalDateTime(String date, DateTimeFormat dateTimeFormat, ZoneId zoneId) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat.getValue());

            // Try to parse as ZonedDateTime first (for patterns with timezone info)
            try {
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(date, formatter.withZone(zoneId));
                return zonedDateTime.toLocalDateTime();
            } catch (Exception e) {
                // If that fails, try parsing as LocalDateTime directly
                return LocalDateTime.parse(date, formatter);
            }
        } catch (Exception e) {
            /* Silent exception - matches your original behavior */
        }
        return null;
    }

    /**
     * Parses the given date string into a LocalDateTime using the specified DateTimeFormat and the system default ZoneId.
     *
     * @param date the date string to parse
     * @param dateTimeFormat the DateTimeFormat to use for parsing
     * @return the parsed LocalDateTime, or null if parsing fails
     */
    public static LocalDateTime parseLocalDateTime(String date, DateTimeFormat dateTimeFormat) {
        return parseLocalDateTime(date, dateTimeFormat, ZoneId.systemDefault());
    }

    /**
     * Formats the given LocalDate into a string using the specified DateTimeFormat and ZoneId.
     *
     * @param date the LocalDate to format
     * @param dateTimeFormat the DateTimeFormat to use for formatting
     * @param zoneId the ZoneId to consider during formatting
     * @return the formatted date string
     */
    public static String formatLocalDate(LocalDate date, DateTimeFormat dateTimeFormat, ZoneId zoneId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat.getValue());
        ZonedDateTime zonedDateTime = date.atStartOfDay(zoneId);
        return zonedDateTime.format(formatter);
    }

    /**
     * Formats the given LocalDate into a string using the specified DateTimeFormat and the system default ZoneId.
     *
     * @param date the LocalDate to format
     * @param dateTimeFormat the DateTimeFormat to use for formatting
     * @return the formatted date string
     */
    public static String formatLocalDate(LocalDate date, DateTimeFormat dateTimeFormat) {
        return formatLocalDate(date, dateTimeFormat, ZoneId.systemDefault());
    }

    /**
     * Formats the given LocalDateTime into a string using the specified DateTimeFormat and ZoneId.
     *
     * @param dateTime the LocalDateTime to format
     * @param dateTimeFormat the DateTimeFormat to use for formatting
     * @param zoneId the ZoneId to consider during formatting
     * @return the formatted date string
     */
    public static String formatLocalDateTime(LocalDateTime dateTime, DateTimeFormat dateTimeFormat, ZoneId zoneId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat.getValue());
        ZonedDateTime zonedDateTime = dateTime.atZone(zoneId);
        return zonedDateTime.format(formatter);
    }

    /**
     * Formats the given LocalDateTime into a string using the specified DateTimeFormat and the system default ZoneId.
     *
     * @param dateTime the LocalDateTime to format
     * @param dateTimeFormat the DateTimeFormat to use for formatting
     * @return the formatted date string
     */
    public static String formatLocalDateTime(LocalDateTime dateTime, DateTimeFormat dateTimeFormat) {
        return formatLocalDateTime(dateTime, dateTimeFormat, ZoneId.systemDefault());
    }

    /**
     * Converts the given LocalDate to epoch seconds using the specified ZoneId.
     *
     * @param localDate the LocalDate to convert
     * @param zoneId the ZoneId to consider during conversion
     * @return the epoch seconds representation of the LocalDate
     */
    public static long toEpochSeconds(LocalDate localDate, ZoneId zoneId) {
        return localDate.atStartOfDay(zoneId).toEpochSecond();
    }

    /**
     * Converts the given LocalDate to epoch seconds using the system default ZoneId.
     *
     * @param localDate the LocalDate to convert
     * @return the epoch seconds representation of the LocalDate
     */
    public static long toEpochSeconds(LocalDate localDate) {
        return toEpochSeconds(localDate, ZoneId.systemDefault());
    }

    /**
     * Creates a LocalDate from the given epoch seconds using the specified ZoneId.
     *
     * @param epochSeconds the epoch seconds to convert
     * @param zoneId the ZoneId to consider during conversion
     * @return the LocalDate representation of the epoch seconds
     */
    public static LocalDate fromEpochSeconds(long epochSeconds, ZoneId zoneId) {
        return LocalDateTime.ofEpochSecond(
                        epochSeconds, 0, zoneId.getRules().getOffset(java.time.Instant.ofEpochSecond(epochSeconds)))
                .toLocalDate();
    }

    /**
     * Creates a LocalDate from the given epoch seconds using the system default ZoneId.
     *
     * @param epochSeconds the epoch seconds to convert
     * @return the LocalDate representation of the epoch seconds
     */
    public static LocalDate fromEpochSeconds(long epochSeconds) {
        return fromEpochSeconds(epochSeconds, ZoneId.systemDefault());
    }

    /**
     * Converts the given LocalDateTime to epoch seconds using the specified ZoneId.
     *
     * @param localDateTime the LocalDateTime to convert
     * @param zoneId the ZoneId to consider during conversion
     * @return the epoch seconds representation of the LocalDateTime
     */
    public static long toEpochSeconds(LocalDateTime localDateTime, ZoneId zoneId) {
        return localDateTime.atZone(zoneId).toEpochSecond();
    }

    /**
     * Converts the given LocalDateTime to epoch seconds using the system default ZoneId.
     *
     * @param localDateTime the LocalDateTime to convert
     * @return the epoch seconds representation of the LocalDateTime
     */
    public static long toEpochSeconds(LocalDateTime localDateTime) {
        return toEpochSeconds(localDateTime, ZoneId.systemDefault());
    }

    /**
     * Creates a LocalDateTime from the given epoch seconds using the specified ZoneId.
     *
     * @param epochSeconds the epoch seconds to convert
     * @param zoneId the ZoneId to consider during conversion
     * @return the LocalDateTime representation of the epoch seconds
     */
    public static LocalDateTime fromEpochSecondsLocalDateTime(long epochSeconds, ZoneId zoneId) {
        return LocalDateTime.ofEpochSecond(
                epochSeconds, 0, zoneId.getRules().getOffset(java.time.Instant.ofEpochSecond(epochSeconds)));
    }

    /**
     * Creates a LocalDateTime from the given epoch seconds using the system default ZoneId.
     *
     * @param epochSeconds the epoch seconds to convert
     * @return the LocalDateTime representation of the epoch seconds
     */
    public static LocalDateTime fromEpochSecondsLocalDateTime(long epochSeconds) {
        return fromEpochSecondsLocalDateTime(epochSeconds, ZoneId.systemDefault());
    }

    /**
     * Compares two LocalDate objects for equality.
     *
     * @param dt1 the first LocalDate to compare
     * @param dt2 the second LocalDate to compare
     * @return true if the two LocalDate objects are equal, false otherwise
     */
    public static boolean compare(LocalDate dt1, LocalDate dt2) {
        if (dt1 == null || dt2 == null) {
            return false;
        }
        return dt1.isEqual(dt2);
    }

    /**
     * Compares two LocalDateTime objects for equality, ignoring milliseconds.
     *
     * @param dt1 the first LocalDateTime to compare
     * @param dt2 the second LocalDateTime to compare
     * @return true if the two LocalDateTime objects are equal ignoring milliseconds, false otherwise
     */
    public static boolean compareIgnoringMS(LocalDateTime dt1, LocalDateTime dt2) {
        if (dt1 == null || dt2 == null) {
            return false;
        }
        return dt1.withNano(0).isEqual(dt2.withNano(0));
    }

    /**
     * Creates a LocalDate with the specified year, month, and day.
     * @param year the year
     * @param month the month
     * @param day the day
     * @return the created LocalDate
     */
    public static LocalDate getLocalDate(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }

    /**
     * Creates a LocalDateTime with the specified year, month, day, hour, minute, and second.
     * @param year the year
     * @param month the month
     * @param day the day
     * @param hour the hour
     * @param minute the minute
     * @param second the second
     * @return the created LocalDateTime
     */
    public static LocalDateTime getLocalDateTime(int year, int month, int day, int hour, int minute, int second) {
        return LocalDateTime.of(year, month, day, hour, minute, second);
    }

    /**
     * Returns the current LocalDate.
     * @return the current LocalDate
     */
    public static LocalDate currentLocalDate() {
        return LocalDate.now();
    }

    /**
     * Returns the current LocalDateTime.
     * @return the current LocalDateTime
     */
    public static LocalDateTime currentLocalDateTime() {
        return LocalDateTime.now();
    }
}
