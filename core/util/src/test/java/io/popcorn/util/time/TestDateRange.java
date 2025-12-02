package io.popcorn.util.time;

import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public class TestDateRange {

    @Test
    public void test_constructor() {
        Date startDate = new Date();
        Date endDate = new Date();

        DateRange dateRange = new DateRange(startDate, endDate);
        Assertions.assertEquals(startDate, dateRange.getStartDate());
        Assertions.assertEquals(endDate, dateRange.getEndDate());
    }

    @Test
    public void test_constructor_1() {
        Date startDate = new Date();

        DateRange dateRange = new DateRange(startDate);
        Assertions.assertEquals(startDate, dateRange.getStartDate());
        Assertions.assertNotNull(dateRange.getEndDate());
    }

    @Test
    public void test_days() {
        Date startDate = Dates.getDate(2024, 11, 1);
        Date endDate = Dates.getDate(2024, 11, 18);

        DateRange dateRange = new DateRange(startDate, endDate);
        Assertions.assertEquals(17, dateRange.days());
    }
}
