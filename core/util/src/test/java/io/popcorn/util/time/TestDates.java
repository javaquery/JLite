package io.popcorn.util.time;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public class TestDates {

  private static final Calendar CALENDAR = Calendar.getInstance();
  private static final String TIMEZONE_SHORT = CALENDAR.getTimeZone().getDisplayName(false, TimeZone.SHORT);

  @Test
  public void test_currentTimeMillis() {
    long expected = System.currentTimeMillis();
    long result = Dates.currentTimeMillis();
    long difference = result - expected;
    Assertions.assertTrue(difference >= 0 && difference < 5);
  }

  @Test
  public void test_addInDate() {
    Date date = Dates.parse("2021-01-19", DatePattern.Y_M_D_1);
    Date expected = Dates.parse("2021-01-20", DatePattern.Y_M_D_1);

    Date result = Dates.addInDate(date, Calendar.DATE, 1);
    Assertions.assertEquals(expected, result);
  }

  @Test
  public void test_addInDate_1() {
    Date date = Dates.parse("2021-01-19", DatePattern.Y_M_D_1);
    Date expected = Dates.parse("2020-12-19", DatePattern.Y_M_D_1);

    Date result = Dates.addInDate(date, Calendar.MONTH, -1);
    Assertions.assertEquals(expected, result);
  }

  @Test
  public void test_increment() {
    Date date = Dates.parse("2021-01-20 10:10:00", DatePattern.Y_M_D__HMS);
    Date expected = Dates.parse("2021-01-20 10:20:00", DatePattern.Y_M_D__HMS);

    Date result = Dates.increment(date, Calendar.MINUTE, 10);
    Assertions.assertEquals(expected, result);
  }

  @Test
  public void test_increment_1() {
    Date date = Dates.parse("2021-01-20 10:10:00", DatePattern.Y_M_D__HMS);
    Date expected = Dates.parse("2021-01-20 10:10:10", DatePattern.Y_M_D__HMS);

    Date result = Dates.increment(date, Calendar.SECOND, -10);
    Assertions.assertEquals(expected, result);
  }

  @Test
  public void test_reduce() {
    Date date = Dates.parse("2021-01-20 10:00:00", DatePattern.Y_M_D__HMS);
    Date expected = Dates.parse("2021-01-20 09:50:00", DatePattern.Y_M_D__HMS);

    Date result = Dates.reduce(date, Calendar.MINUTE, -10);
    Assertions.assertEquals(expected, result);
  }

  @Test
  public void test_reduce_1() {
    Date date = Dates.parse("2021-01-20 10:00:10", DatePattern.Y_M_D__HMS);
    Date expected = Dates.parse("2021-01-20 10:00:00", DatePattern.Y_M_D__HMS);

    Date result = Dates.reduce(date, Calendar.SECOND, 10);
    Assertions.assertEquals(expected, result);
  }

  @Test
  public void test_addInCurrentDate() {
    Date date = Dates.addInCurrentDate(Calendar.YEAR, 1);
    CALENDAR.setTime(date);

    int currentYear = Calendar.getInstance().get(Calendar.YEAR);

    Assertions.assertEquals(currentYear + 1, CALENDAR.get(Calendar.YEAR));
  }

  @Test
  public void test_addInCurrentDate_1() {
    Date date = Dates.addInCurrentDate(Calendar.YEAR, -5);
    CALENDAR.setTime(date);

    int currentYear = Calendar.getInstance().get(Calendar.YEAR);

    Assertions.assertEquals(currentYear - 5, CALENDAR.get(Calendar.YEAR));
  }

  @Test
  public void test_parse() {
    Date date =
        Dates.parse(
            "2021-01-20T10:10:00+00:00", DatePattern.Y_M_D_T_HMSX, TimeZone.getTimeZone("IST"));
    CALENDAR.setTime(date); // Output: Wed Jan 20 04:40:00 UTC 2021

    Assertions.assertEquals(2021, CALENDAR.get(Calendar.YEAR));
    Assertions.assertEquals(1, CALENDAR.get(Calendar.MONTH) + 1);
    Assertions.assertEquals(20, CALENDAR.get(Calendar.DATE));
    Assertions.assertEquals(15, CALENDAR.get(Calendar.HOUR_OF_DAY));
    Assertions.assertEquals(40, CALENDAR.get(Calendar.MINUTE));
    Assertions.assertEquals(0, CALENDAR.get(Calendar.SECOND));
  }

  @Test
  public void test_parse_1() {
    Date date = Dates.parse("2021-01-19", DatePattern.Y_M_D_1);
    CALENDAR.setTime(date);

    Assertions.assertEquals(2021, CALENDAR.get(Calendar.YEAR));
    Assertions.assertEquals(1, CALENDAR.get(Calendar.MONTH) + 1);
    Assertions.assertEquals(19, CALENDAR.get(Calendar.DATE));
  }

  @Test
  public void test_parse_2() {
    Date date = Dates.parse("2021-19", DatePattern.Y_M_D_1);
    Assertions.assertNull(date);
  }

  @Test
  public void test_format() {
    Date date =
        Dates.parse(
            "2021-01-20T04:40:00+00:00", DatePattern.Y_M_D_T_HMSX, TimeZone.getTimeZone("UTC"));
    String result = Dates.format(date, DatePattern.Y_M_D_T_HMSX, TimeZone.getTimeZone("IST"));
    Assertions.assertEquals("2021-01-20T10:10:00+05:30", result);
  }

  @Test
  public void test_format_1() {
    Date date = Dates.parse("2021-01-20T10:10:00", DatePattern.Y_M_D_T_HMS);
    String result = Dates.format(date, DatePattern.Y_M_D_HMS);
    Assertions.assertEquals("20210120101000", result);
  }

  @Test
  public void test_addInDateAndFormat() {
    Date date = Dates.parse("2021-01-20T10:10:00", DatePattern.Y_M_D_T_HMS);
    String result = Dates.addInDateAndFormat(date, Calendar.MONTH, -1, DatePattern.Y_M_D_HMS);
    Assertions.assertEquals("20201220101000", result);
  }

  @Test
  public void test_dateToEpochSeconds() {
    Date date = Dates.parse("2021-01-20T10:10:00", DatePattern.Y_M_D_T_HMS);
    long result = Dates.epochSeconds(date);
    Assertions.assertEquals(1611117600, result);
  }

  @Test
  public void test_getDate() {
    Date result = Dates.getDate(2020, 1, 20, 10, 10, 10);
    Assertions.assertEquals("Mon Jan 20 10:10:10 " + TIMEZONE_SHORT + " 2020", result.toString());
  }

  @Test
  public void test_getDate_1() {
    Date result = Dates.getDate(2020, 1, 20);
    Assertions.assertEquals("Mon Jan 20 00:00:00 " + TIMEZONE_SHORT + " 2020", result.toString());
  }

  @Test
  public void test_current(){
    Assertions.assertNotNull(Dates.current());
  }

  @Test
  public void test_Month() {
    Assertions.assertEquals(1, Dates.Month.JANUARY.getValue());
  }

  @Test
  public void test_Month_currentMonth() {
    int expected = Calendar.getInstance().get(Calendar.MONTH) + 1;

    Assertions.assertEquals(expected, Dates.Month.currentMonth().getValue());
  }
}
