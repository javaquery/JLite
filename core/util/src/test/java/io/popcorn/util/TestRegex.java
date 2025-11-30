package io.popcorn.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public class TestRegex {

  @Test
  public void test_isNumber() {
    Assertions.assertTrue(Regex.isNumber("0"));
    Assertions.assertTrue(Regex.isNumber("25.34"));
    Assertions.assertTrue(Regex.isNumber("-98.98"));
    Assertions.assertFalse(Regex.isNumber("123AB"));
    Assertions.assertFalse(Regex.isNumber(null));
  }

  @Test
  public void test_isAlphaNumeric() {
    Assertions.assertTrue(Regex.isAlphaNumeric("123AB"));
    Assertions.assertTrue(Regex.isAlphaNumeric("123"));
    Assertions.assertTrue(Regex.isAlphaNumeric("AB"));
    Assertions.assertFalse(Regex.isAlphaNumeric("123#AB"));
    Assertions.assertFalse(Regex.isAlphaNumeric("123 AB"));
  }

  @Test
  public void test_isValidEmail() {
    Assertions.assertTrue(Regex.isValidEmail("vicky.thakor@javaquery.com"));
    Assertions.assertTrue(Regex.isValidEmail("vicky.thakor@123.com"));
    Assertions.assertFalse(Regex.isValidEmail("vicky.thakor@javaquery com"));
    Assertions.assertFalse(Regex.isValidEmail("vicky.thakor@javaquery"));
    Assertions.assertTrue(Regex.isValidEmail("fogawa@js6.so-net.ne.jp"));
  }
}
