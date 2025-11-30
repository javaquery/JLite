package io.popcorn.util.time;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public class TestLocalDates {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final ZoneId UTC = ZoneId.of("UTC");

    @Test
    public void test_addInLocalDate() {
        LocalDate date = LocalDate.of(2021, 1, 19);
        LocalDate expected = LocalDate.of(2021, 1, 20);

        LocalDate result = LocalDates.addInLocalDate(date, Calendar.DAY_OF_MONTH, 1);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void test_addInLocalDate_subtractMonth() {
        LocalDate date = LocalDate.of(2021, 1, 19);
        LocalDate expected = LocalDate.of(2020, 12, 19);

        LocalDate result = LocalDates.addInLocalDate(date, Calendar.MONTH, -1);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void test_addInLocalDate_addYear() {
        LocalDate date = LocalDate.of(2021, 1, 19);
        LocalDate expected = LocalDate.of(2022, 1, 19);

        LocalDate result = LocalDates.addInLocalDate(date, Calendar.YEAR, 1);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void test_addInLocalDate_nullDate() {
        Assertions.assertNull(LocalDates.addInLocalDate(null, Calendar.DAY_OF_MONTH, 1));
    }

    @Test
    public void test_addInLocalDate_unsupportedType() {
        LocalDate date = LocalDate.of(2021, 1, 19);
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocalDates.addInLocalDate(date, Calendar.HOUR, 1));
    }

    @Test
    public void test_addInLocalDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2021, 1, 20, 10, 10, 0);
        LocalDateTime expected = LocalDateTime.of(2021, 1, 20, 10, 20, 0);

        LocalDateTime result = LocalDates.addInLocalDateTime(dateTime, Calendar.MINUTE, 10);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void test_addInLocalDateTime_addDays() {
        LocalDateTime dateTime = LocalDateTime.of(2021, 1, 20, 10, 10, 0);
        LocalDateTime expected = LocalDateTime.of(2021, 1, 21, 10, 10, 0);

        LocalDateTime result = LocalDates.addInLocalDateTime(dateTime, Calendar.DAY_OF_MONTH, 1);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void test_addInLocalDateTime_subtractSeconds() {
        LocalDateTime dateTime = LocalDateTime.of(2021, 1, 20, 10, 10, 10);
        LocalDateTime expected = LocalDateTime.of(2021, 1, 20, 10, 10, 0);

        LocalDateTime result = LocalDates.addInLocalDateTime(dateTime, Calendar.SECOND, -10);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void test_addInLocalDateTime_addWeeks() {
        LocalDateTime dateTime = LocalDateTime.of(2021, 1, 20, 10, 10, 0);
        LocalDateTime expected = LocalDateTime.of(2021, 1, 27, 10, 10, 0);

        LocalDateTime result = LocalDates.addInLocalDateTime(dateTime, Calendar.WEEK_OF_YEAR, 1);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void test_addInLocalDateTime_addHours() {
        LocalDateTime dateTime = LocalDateTime.of(2021, 1, 20, 10, 10, 0);
        LocalDateTime expected = LocalDateTime.of(2021, 1, 20, 12, 10, 0);

        LocalDateTime result = LocalDates.addInLocalDateTime(dateTime, Calendar.HOUR, 2);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void test_addInLocalDateTime_addHoursOfDay() {
        LocalDateTime dateTime = LocalDateTime.of(2021, 1, 20, 10, 10, 0);
        LocalDateTime expected = LocalDateTime.of(2021, 1, 20, 13, 10, 0);

        LocalDateTime result = LocalDates.addInLocalDateTime(dateTime, Calendar.HOUR_OF_DAY, 3);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void test_addInLocalDateTime_nullDateTime() {
        Assertions.assertNull(LocalDates.addInLocalDateTime(null, Calendar.DAY_OF_MONTH, 1));
    }

    @Test
    public void test_addInLocalDateTime_unsupportedType() {
        LocalDateTime dateTime = LocalDateTime.of(2021, 1, 20, 10, 10, 0);
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocalDates.addInLocalDateTime(dateTime, Calendar.MILLISECOND, 1));
    }

    @Test
    public void test_addInCurrentLocalDate() {
        LocalDate result = LocalDates.addInCurrentLocalDate(Calendar.YEAR, 1);
        int currentYear = LocalDate.now().getYear();
        Assertions.assertEquals(currentYear + 1, result.getYear());
    }

    @Test
    public void test_addInCurrentLocalDate_subtractYears() {
        LocalDate result = LocalDates.addInCurrentLocalDate(Calendar.YEAR, -5);
        int currentYear = LocalDate.now().getYear();
        Assertions.assertEquals(currentYear - 5, result.getYear());
    }

    @Test
    public void test_addInCurrentLocalDateTime() {
        LocalDateTime result = LocalDates.addInCurrentLocalDateTime(Calendar.DAY_OF_MONTH, 1);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isAfter(LocalDateTime.now()) || result.isEqual(LocalDateTime.now()));
    }

    @Test
    public void test_parseLocalDate() {
        String dateString = "2021-01-19";
        LocalDate result = LocalDates.parseLocalDate(dateString, DatePattern.Y_M_D_1);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2021, result.getYear());
        Assertions.assertEquals(1, result.getMonthValue());
        Assertions.assertEquals(19, result.getDayOfMonth());
    }

    @Test
    public void test_parseLocalDate_withZoneId() {
        String dateString = "2021-01-19";
        LocalDate result = LocalDates.parseLocalDate(dateString, DatePattern.Y_M_D_1, UTC);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2021, result.getYear());
        Assertions.assertEquals(1, result.getMonthValue());
        Assertions.assertEquals(19, result.getDayOfMonth());
    }

    @Test
    public void test_parseLocalDate_invalidDate() {
        String dateString = "2021-19";
        LocalDate result = LocalDates.parseLocalDate(dateString, DatePattern.Y_M_D_1);
        Assertions.assertNull(result);
    }

    @Test
    public void test_parseLocalDateTime() {
        String dateString = "2021-01-20T10:10:00";
        LocalDateTime result = LocalDates.parseLocalDateTime(dateString, DatePattern.Y_M_D_T_HMS);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2021, result.getYear());
        Assertions.assertEquals(1, result.getMonthValue());
        Assertions.assertEquals(20, result.getDayOfMonth());
        Assertions.assertEquals(10, result.getHour());
        Assertions.assertEquals(10, result.getMinute());
        Assertions.assertEquals(0, result.getSecond());
    }

    @Test
    public void test_parseLocalDateTime_withZoneId() {
        String dateString = "2021-01-20T10:10:00";
        LocalDateTime result = LocalDates.parseLocalDateTime(dateString, DatePattern.Y_M_D_T_HMS, IST);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2021, result.getYear());
        Assertions.assertEquals(1, result.getMonthValue());
        Assertions.assertEquals(20, result.getDayOfMonth());
        Assertions.assertEquals(10, result.getHour());
        Assertions.assertEquals(10, result.getMinute());
    }

    @Test
    public void test_parseLocalDateTime_invalidDate() {
        String dateString = "invalid-date";
        LocalDateTime result = LocalDates.parseLocalDateTime(dateString, DatePattern.Y_M_D_T_HMS);
        Assertions.assertNull(result);
    }

    @Test
    public void test_formatLocalDate() {
        LocalDate date = LocalDate.of(2021, 1, 19);
        String result = LocalDates.formatLocalDate(date, DatePattern.Y_M_D_1);
        Assertions.assertEquals("2021-01-19", result);
    }

    @Test
    public void test_formatLocalDate_withZoneId() {
        LocalDate date = LocalDate.of(2021, 1, 19);
        String result = LocalDates.formatLocalDate(date, DatePattern.Y_M_D_1, UTC);
        Assertions.assertEquals("2021-01-19", result);
    }

    @Test
    public void test_formatLocalDate_differentFormat() {
        LocalDate date = LocalDate.of(2021, 1, 19);
        String result = LocalDates.formatLocalDate(date, DatePattern.Y_M_D);
        Assertions.assertEquals("20210119", result);
    }

    @Test
    public void test_formatLocalDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2021, 1, 20, 10, 10, 0);
        String result = LocalDates.formatLocalDateTime(dateTime, DatePattern.Y_M_D_T_HMS);
        Assertions.assertEquals("2021-01-20T10:10:00", result);
    }

    @Test
    public void test_formatLocalDateTime_withZoneId() {
        LocalDateTime dateTime = LocalDateTime.of(2021, 1, 20, 10, 10, 0);
        String result = LocalDates.formatLocalDateTime(dateTime, DatePattern.Y_M_D_T_HMS, UTC);
        Assertions.assertEquals("2021-01-20T10:10:00", result);
    }

    @Test
    public void test_formatLocalDateTime_differentFormat() {
        LocalDateTime dateTime = LocalDateTime.of(2021, 1, 20, 10, 10, 0);
        String result = LocalDates.formatLocalDateTime(dateTime, DatePattern.Y_M_D_HMS);
        Assertions.assertEquals("20210120101000", result);
    }

    @Test
    public void test_toEpochSeconds_localDate() {
        LocalDate date = LocalDate.of(2021, 1, 20);
        long result = LocalDates.toEpochSeconds(date);
        Assertions.assertTrue(result > 0);
    }

    @Test
    public void test_toEpochSeconds_localDate_withZoneId() {
        LocalDate date = LocalDate.of(2021, 1, 20);
        long resultUTC = LocalDates.toEpochSeconds(date, UTC);
        long resultIST = LocalDates.toEpochSeconds(date, IST);

        Assertions.assertTrue(resultUTC > 0);
        Assertions.assertTrue(resultIST > 0);
        // IST is ahead of UTC, so epoch seconds at start of day will be different
        Assertions.assertNotEquals(resultUTC, resultIST);
    }

    @Test
    public void test_fromEpochSeconds_localDate() {
        long epochSeconds = 1611100800L; // 2021-01-20 00:00:00 UTC
        LocalDate result = LocalDates.fromEpochSeconds(epochSeconds, UTC);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2021, result.getYear());
        Assertions.assertEquals(1, result.getMonthValue());
        Assertions.assertEquals(20, result.getDayOfMonth());
    }

    @Test
    public void test_fromEpochSeconds_localDate_defaultZone() {
        long epochSeconds = 1611100800L;
        LocalDate result = LocalDates.fromEpochSeconds(epochSeconds);
        Assertions.assertNotNull(result);
    }

    @Test
    public void test_toEpochSeconds_localDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2021, 1, 20, 10, 10, 0);
        long result = LocalDates.toEpochSeconds(dateTime);
        Assertions.assertTrue(result > 0);
    }

    @Test
    public void test_toEpochSeconds_localDateTime_withZoneId() {
        LocalDateTime dateTime = LocalDateTime.of(2021, 1, 20, 10, 10, 0);
        long resultUTC = LocalDates.toEpochSeconds(dateTime, UTC);
        long resultIST = LocalDates.toEpochSeconds(dateTime, IST);

        Assertions.assertTrue(resultUTC > 0);
        Assertions.assertTrue(resultIST > 0);
        Assertions.assertNotEquals(resultUTC, resultIST);
    }

    @Test
    public void test_fromEpochSecondsLocalDateTime() {
        long epochSeconds = 1611137400L; // 2021-01-20 10:10:00 UTC
        LocalDateTime result = LocalDates.fromEpochSecondsLocalDateTime(epochSeconds, UTC);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2021, result.getYear());
        Assertions.assertEquals(1, result.getMonthValue());
        Assertions.assertEquals(20, result.getDayOfMonth());
        Assertions.assertEquals(10, result.getHour());
        Assertions.assertEquals(10, result.getMinute());
    }

    @Test
    public void test_fromEpochSecondsLocalDateTime_defaultZone() {
        long epochSeconds = 1611137400L;
        LocalDateTime result = LocalDates.fromEpochSecondsLocalDateTime(epochSeconds);
        Assertions.assertNotNull(result);
    }

    @Test
    public void test_epochSeconds_roundTrip_localDate() {
        LocalDate original = LocalDate.of(2021, 1, 20);
        long epochSeconds = LocalDates.toEpochSeconds(original, UTC);
        LocalDate result = LocalDates.fromEpochSeconds(epochSeconds, UTC);
        Assertions.assertEquals(original, result);
    }

    @Test
    public void test_epochSeconds_roundTrip_localDateTime() {
        LocalDateTime original = LocalDateTime.of(2021, 1, 20, 10, 10, 0);
        long epochSeconds = LocalDates.toEpochSeconds(original, UTC);
        LocalDateTime result = LocalDates.fromEpochSecondsLocalDateTime(epochSeconds, UTC);
        Assertions.assertEquals(original, result);
    }

    @Test
    public void test_compare_equalDates() {
        LocalDate date1 = LocalDate.of(2021, 1, 20);
        LocalDate date2 = LocalDate.of(2021, 1, 20);

        boolean result = LocalDates.compare(date1, date2);
        Assertions.assertTrue(result);
    }

    @Test
    public void test_compare_differentDates() {
        LocalDate date1 = LocalDate.of(2021, 1, 20);
        LocalDate date2 = LocalDate.of(2021, 1, 21);

        boolean result = LocalDates.compare(date1, date2);
        Assertions.assertFalse(result);
    }

    @Test
    public void test_compare_nullFirstDate() {
        LocalDate date2 = LocalDate.of(2021, 1, 20);
        boolean result = LocalDates.compare(null, date2);
        Assertions.assertFalse(result);
    }

    @Test
    public void test_compare_nullSecondDate() {
        LocalDate date1 = LocalDate.of(2021, 1, 20);
        boolean result = LocalDates.compare(date1, null);
        Assertions.assertFalse(result);
    }

    @Test
    public void test_compare_bothNull() {
        boolean result = LocalDates.compare(null, null);
        Assertions.assertFalse(result);
    }

    @Test
    public void test_compareIgnoringMS_equalDateTimes() {
        LocalDateTime dt1 = LocalDateTime.of(2021, 1, 20, 10, 10, 0, 123456789);
        LocalDateTime dt2 = LocalDateTime.of(2021, 1, 20, 10, 10, 0, 987654321);

        boolean result = LocalDates.compareIgnoringMS(dt1, dt2);
        Assertions.assertTrue(result);
    }

    @Test
    public void test_compareIgnoringMS_differentDateTimes() {
        LocalDateTime dt1 = LocalDateTime.of(2021, 1, 20, 10, 10, 0);
        LocalDateTime dt2 = LocalDateTime.of(2021, 1, 20, 10, 10, 1);

        boolean result = LocalDates.compareIgnoringMS(dt1, dt2);
        Assertions.assertFalse(result);
    }

    @Test
    public void test_compareIgnoringMS_nullFirstDateTime() {
        LocalDateTime dt2 = LocalDateTime.of(2021, 1, 20, 10, 10, 0);
        boolean result = LocalDates.compareIgnoringMS(null, dt2);
        Assertions.assertFalse(result);
    }

    @Test
    public void test_compareIgnoringMS_nullSecondDateTime() {
        LocalDateTime dt1 = LocalDateTime.of(2021, 1, 20, 10, 10, 0);
        boolean result = LocalDates.compareIgnoringMS(dt1, null);
        Assertions.assertFalse(result);
    }

    @Test
    public void test_compareIgnoringMS_bothNull() {
        boolean result = LocalDates.compareIgnoringMS(null, null);
        Assertions.assertFalse(result);
    }

    @Test
    public void test_getLocalDate() {
        LocalDate result = LocalDates.getLocalDate(2021, 1, 20);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2021, result.getYear());
        Assertions.assertEquals(1, result.getMonthValue());
        Assertions.assertEquals(20, result.getDayOfMonth());
    }

    @Test
    public void test_getLocalDateTime() {
        LocalDateTime result = LocalDates.getLocalDateTime(2021, 1, 20, 10, 10, 30);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2021, result.getYear());
        Assertions.assertEquals(1, result.getMonthValue());
        Assertions.assertEquals(20, result.getDayOfMonth());
        Assertions.assertEquals(10, result.getHour());
        Assertions.assertEquals(10, result.getMinute());
        Assertions.assertEquals(30, result.getSecond());
    }

    @Test
    public void test_currentLocalDate() {
        LocalDate result = LocalDates.currentLocalDate();
        LocalDate now = LocalDate.now();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(now.getYear(), result.getYear());
        Assertions.assertEquals(now.getMonthValue(), result.getMonthValue());
        Assertions.assertEquals(now.getDayOfMonth(), result.getDayOfMonth());
    }

    @Test
    public void test_currentLocalDateTime() {
        LocalDateTime result = LocalDates.currentLocalDateTime();
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    public void test_parseAndFormat_roundTrip_localDate() {
        String originalDate = "2021-01-19";
        LocalDate parsed = LocalDates.parseLocalDate(originalDate, DatePattern.Y_M_D_1);
        String formatted = LocalDates.formatLocalDate(parsed, DatePattern.Y_M_D_1);
        Assertions.assertEquals(originalDate, formatted);
    }

    @Test
    public void test_parseAndFormat_roundTrip_localDateTime() {
        String originalDateTime = "2021-01-20T10:10:00";
        LocalDateTime parsed = LocalDates.parseLocalDateTime(originalDateTime, DatePattern.Y_M_D_T_HMS);
        String formatted = LocalDates.formatLocalDateTime(parsed, DatePattern.Y_M_D_T_HMS);
        Assertions.assertEquals(originalDateTime, formatted);
    }

    @Test
    public void test_addInLocalDate_subtractDays() {
        LocalDate date = LocalDate.of(2021, 1, 20);
        LocalDate expected = LocalDate.of(2021, 1, 15);

        LocalDate result = LocalDates.addInLocalDate(date, Calendar.DAY_OF_MONTH, -5);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void test_addInLocalDateTime_subtractMonth() {
        LocalDateTime dateTime = LocalDateTime.of(2021, 1, 20, 10, 10, 0);
        LocalDateTime expected = LocalDateTime.of(2020, 12, 20, 10, 10, 0);

        LocalDateTime result = LocalDates.addInLocalDateTime(dateTime, Calendar.MONTH, -1);
        Assertions.assertEquals(expected, result);
    }
}

