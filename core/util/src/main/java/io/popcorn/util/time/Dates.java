package io.popcorn.util.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public final class Dates {

    public static final TimeZone SYSTEM_TIMEZONE = TimeZone.getDefault();

    private Dates() {}

    /** @return current time in milliseconds by using System.currentTimeMillis() */
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * Adds or subtracts the specified amount of time to the given Date. For example, to subtract 5
     * days from the given {@code Date} you can achieve it by calling:
     *
     * <p><code>addInDate(new Date(), Calendar.DAY_OF_MONTH, -5)</code>.
     *
     * @param date a {@code Date} to Adds or subtracts the specified amount of time
     * @param type the calendar field. For example, {@code Calendar.DAY_OF_MONTH}
     * @param amount the amount of date or time to be added to the field.
     * @return Returns {@code Date} with addition or subtraction
     */
    public static Date addInDate(Date date, int type, int amount) {
        Calendar calendar = Calendar.getInstance();
        if (Objects.nonNull(date)) {
            calendar.setTime(date);
            calendar.add(type, amount);
        }
        return calendar.getTime();
    }

    /**
     * Adds or subtracts the specified amount of time to current {@code Date}. For example, to
     * subtract 5 days from current {@code Date} you can achieve it by calling:
     *
     * <p><code>addInCurrentDate(Calendar.DAY_OF_MONTH, -5)</code>.
     *
     * @param type the calendar field. For example, {@code Calendar.DAY_OF_MONTH}
     * @param amount the amount of date or time to be added to the field.
     * @return Returns current {@code Date} with addition or subtraction
     */
    public static Date addInCurrentDate(int type, int amount) {
        return addInDate(new Date(), type, amount);
    }

    /**
     * Adds the specified amount of time to the given Date. For example, to add 5 days from the given
     * {@code Date} you can achieve it by calling:
     *
     * <p><code>increment(new Date(), Calendar.DAY_OF_MONTH, 5)</code>.
     *
     * @param date a {@code Date} to Adds the specified amount of time
     * @param type the calendar field. For example, {@code Calendar.DAY_OF_MONTH}
     * @param amount the amount of date or time to be added to the field.
     * @return Returns {@code Date} with addition
     * @see Dates#addInDate(Date, int, int)
     */
    public static Date increment(Date date, int type, int amount) {
        return addInDate(date, type, Math.abs(amount));
    }

    /**
     * Subtracts the specified amount of time to the given Date. For example, to subtracts -5 days
     * from the given {@code Date} you can achieve it by calling:
     *
     * <p><code>increment(new Date(), Calendar.DAY_OF_MONTH, -5)</code>.
     *
     * @param date a {@code Date} to Subtracts the specified amount of time
     * @param type the calendar field. For example, {@code Calendar.DAY_OF_MONTH}
     * @param amount the amount of date or time to be subtracts to the field.
     * @return Returns {@code Date} with subtraction.
     * @see Dates#addInDate(Date, int, int)
     */
    public static Date reduce(Date date, int type, int amount) {
        return addInDate(date, type, -Math.abs(amount));
    }

    /**
     * Returns {@code SimpleDateFormat} with given {@code DateTimeFormat} and {@code Timezone}
     *
     * @param dateTimeFormat a {@code DateTimeFormat} to set for {@code SimpleDateFormat}
     * @param timeZone a {@code Timezone} to set for {@code SimpleDateFormat}
     * @return Returns {@code SimpleDateFormat} with given {@code DateTimeFormat} and {@code Timezone}
     */
    private static SimpleDateFormat getSimpleDateFormat(DateTimeFormat dateTimeFormat, TimeZone timeZone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateTimeFormat.getValue());
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat;
    }

    /**
     * Returns Date object of given String date using provided timezone, in case of ParseException it
     * returns {@code null}.
     *
     * @param date a String date to parse
     * @param dateTimeFormat pattern to parse the date
     * @param timeZone timezone to parse the date
     * @return Returns Date object of given String date using provided timezone, in case of
     *     ParseException it returns {@code null}.
     */
    public static Date parse(String date, DateTimeFormat dateTimeFormat, TimeZone timeZone) {
        SimpleDateFormat simpleDateFormat = getSimpleDateFormat(dateTimeFormat, timeZone);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            /* Silent exception */
        }
        return null;
    }

    /**
     * Returns Date object of given String date using system timezone, in case of ParseException it
     * returns {@code null}.
     *
     * @param date a String date to parse
     * @param dateTimeFormat pattern to parse the date
     * @return Returns Date object of given String date using system timezone, in case of
     *     ParseException it returns {@code null}.
     */
    public static Date parse(String date, DateTimeFormat dateTimeFormat) {
        return parse(date, dateTimeFormat, SYSTEM_TIMEZONE);
    }

    /**
     * Returns {@code Date} object in String, formatted by given {@code DateTimeFormat} and {@code
     * TimeZone}
     *
     * @param date a {@code Date} object to format
     * @param dateTimeFormat pattern to format {@code Date}
     * @param timeZone {@code TimeZone} to use while formatting {@code Date}
     * @return Returns {@code Date} object in String, formatted by given {@code DateTimeFormat} and
     *     {@code TimeZone}
     */
    public static String format(Date date, DateTimeFormat dateTimeFormat, TimeZone timeZone) {
        SimpleDateFormat simpleDateFormat = getSimpleDateFormat(dateTimeFormat, timeZone);
        return simpleDateFormat.format(date);
    }

    /**
     * Returns {@code Date} object in String, formatted by given {@code DateTimeFormat} and using
     * system {@code TimeZone}
     *
     * @param date a {@code Date} object to format
     * @param dateTimeFormat pattern to format {@code Date}
     * @return Returns {@code Date} object in String, formatted by given {@code DateTimeFormat} and
     *     using system {@code TimeZone}
     */
    public static String format(Date date, DateTimeFormat dateTimeFormat) {
        return format(date, dateTimeFormat, SYSTEM_TIMEZONE);
    }

    /**
     * Returns String {@code Date} by performing two operation on {@code Date} object, {@link
     * Dates#addInDate(Date, int, int)} and {@link Dates#format(Date, DateTimeFormat)}
     *
     * @param date a {@code Date} object to format
     * @param type the calendar field. For example, {@code Calendar.DAY_OF_MONTH}
     * @param amount the amount of date or time to be added to the field.
     * @param dateTimeFormat pattern to format {@code Date}
     * @return Returns String {@code Date} by performing two operation on {@code Date} object, {@link
     *     Dates#addInDate(Date, int, int)} and {@link Dates#format(Date, DateTimeFormat)}
     * @see Dates#addInDate(Date, int, int)
     * @see Dates#format(Date, DateTimeFormat)
     */
    public static String addInDateAndFormat(Date date, int type, int amount, DateTimeFormat dateTimeFormat) {
        return format(addInDate(date, type, amount), dateTimeFormat);
    }

    /**
     * Returns provided {@code Date} object's epoch seconds
     *
     * @param date a {@code Date} to get epoch seconds of.
     * @return Returns provided {@code Date} object's epoch seconds
     */
    public static long epochSeconds(Date date) {
        return date.getTime() / 1000;
    }

    /**
     * Returns {@code Date} with given year, month, day, hour, minute, seconds
     *
     * @param year year to set in date
     * @param month month to set in date. Month value is 1-12. e.g., 1 for January.
     * @param day day to set in date
     * @param hour hour to set in date
     * @param minute minute to set in date
     * @param seconds seconds to set in date
     * @return Returns {@code Date} with given year, month, day, hour, minute, seconds
     */
    public static Date getDate(int year, int month, int day, int hour, int minute, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, --month, day, hour, minute, seconds);
        return calendar.getTime();
    }

    /**
     * @return current Date object;
     */
    public static Date current() {
        return new Date();
    }

    /**
     * Returns {@code Date} with given year, month, day
     *
     * @param year year to set in date
     * @param month month to set in date
     * @param day day to set in date
     * @return Returns {@code Date} with given year, month, day
     */
    public static Date getDate(int year, int month, int day) {
        return getDate(year, month, day, 0, 0, 0);
    }

    public enum Month {
        JANUARY(1),
        FEBRUARY(2),
        MARCH(3),
        APRIL(4),
        MAY(5),
        JUNE(6),
        JULY(7),
        AUGUST(8),
        SEPTEMBER(9),
        OCTOBER(10),
        NOVEMBER(11),
        DECEMBER(12);

        private static final Calendar calendar = Calendar.getInstance();
        private final int value;

        Month(int value) {
            this.value = value;
        }

        public static Month currentMonth() {
            return Month.values()[calendar.get(Calendar.MONTH)];
        }

        public int getValue() {
            return value;
        }
    }
}
