package io.popcorn.util.time;

/**
 * https//docs.datastax.com/en/astra-cql/doc/cql/cql_reference/refDateTimeFormats.html
 * @author vicky.thakor
 * @since 1.0.0
 */
public enum DatePattern implements DateTimeFormat {
  Y_M_D("yyyyMMdd"),
  Y_M_D_HMS("yyyyMMddHHmmss"),
  Y_M_D_T__HMSZ("yyyyMMdd'T'HHmmss'Z'"),

  Y_M_D_1("yyyy-MM-dd"),
  Y_M_D__HMS("yyyy-MM-dd HH:mm:ss"),
  Y_M_D_HM("yyyy-MM-dd HH:mm"),
  Y_M_D_HMZ("yyyy-MM-dd HH:mm'Z'"),
  Y_M_D_HMSZ("yyyy-MM-dd HH:mm:ssZ"),
  Y_M_D_HMS_Z("yyyy-MM-dd HH:mm:ss'Z'"),
  Y_M_D_T_HMS("yyyy-MM-dd'T'HH:mm:ss"),
  Y_M_D_T_HMSX("yyyy-MM-dd'T'HH:mm:ssXXX"),
  Y_M_D_T_HMSZ("yyyy-MM-dd'T'HH:mm:ss'Z'"),
  Y_M_D_T_HMSSSSSSSZ("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'"),
  Y_M_D_T_HMSSSSSSSHM("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS-HH:MM"),
  Y_M_D_T_HMSSSSSSSX("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX"),
  Y_M_D_T_HMSSSSZ("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),

  M_D_Y("MM-dd-yyyy"),
  M_D_Y_1("MM/dd/yyyy"),
  M_D_Y_HMS("MM/dd/yyyy HH:mm:ss"),
  M_D_Y_HMSA("M/dd/yyyy hh:mm:ss a"),

  D_M_Y("dd-MM-yyyy"),
  D_M_Y_HMSA("dd MMM yyyy hh:mm:ss a"),
  D_M_Y_HMS("dd/MM/yyyy HH:mm:ss"),
  MMM_DD_YYYY("MMM dd, yyyy"),
  E_D_M_Y_HMS("EEE, dd MMM yyyy HH:mm:ss +0000");

  private final String value;

  DatePattern(String value) {
    this.value = value;
  }

  @Override
  public String getValue() {
    return value;
  }
}
