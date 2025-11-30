package io.popcorn.util.time;

import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public final class DateRange {

  private Date startDate;
  private Date endDate;

  private DateRange() {}

  public DateRange(Date startDate, Date endDate) {
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public DateRange(Date startDate) {
    this(startDate, new Date());
  }

  public Date getStartDate() {
    return startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public long days(){
    return ChronoUnit.DAYS.between(startDate.toInstant(), endDate.toInstant());
  }
}
