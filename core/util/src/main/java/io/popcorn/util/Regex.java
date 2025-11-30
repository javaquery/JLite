package io.popcorn.util;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author vicky.thakor
 * @since 1.0
 */
public final class Regex {

  public static final String REGEX_NUMBER = "^-?[0-9]\\d*(\\.\\d+)?$";
  public static final String REGEX_ALPHA_NUMERIC = "^[a-zA-Z0-9]*$";
  public static final String REGEX_EMAIL =
      "^[a-zA-Z0-9.!#$%&'*+\\/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))[^_@!*]*?$";
  public static final Pattern EMAIL_PATTERN = Pattern.compile(REGEX_EMAIL);

  private Regex() {}

  /**
   * Returns {@code true} if the provided String is valid number otherwise returns {@code false}.
   *
   * @param value a String to be checked against number regular expression
   * @return Returns {@code true} if the provided String is valid number otherwise returns {@code
   *     false}.
   */
  public static boolean isNumber(String value) {
    return Objects.nonNull(value) && value.matches(Regex.REGEX_NUMBER);
  }

  /**
   * Returns {@code true} if the provided String is valid alpha numeric otherwise returns {@code
   * false}.
   *
   * @param value a String to be checked against alpha numeric regular expression
   * @return Returns {@code true} if the provided String is valid alpha numeric otherwise returns
   *     {@code false}.
   */
  public static boolean isAlphaNumeric(String value) {
    return Objects.nonNull(value) && value.matches(Regex.REGEX_ALPHA_NUMERIC);
  }

  /**
   * Returns {@code true} if the provided String is valid email otherwise returns {@code false}.
   *
   * @param value a String to be checked against email regular expression
   * @return Returns {@code true} if the provided String is valid email otherwise returns {@code
   *     false}.
   */
  public static boolean isValidEmail(String value) {
    return Objects.nonNull(value) && Regex.EMAIL_PATTERN.matcher(value).matches();
  }
}
