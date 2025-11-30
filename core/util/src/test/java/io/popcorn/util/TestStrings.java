package io.popcorn.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public class TestStrings {

  private static final String HELLO = "Hello World!";
  private static final String DEFAULT_VALUE = "Default";

  @Test
  public void test_nullOrEmpty() {
    Assertions.assertTrue(Strings.nullOrEmpty(null));
    Assertions.assertTrue(Strings.nullOrEmpty(""));
    Assertions.assertFalse(Strings.nullOrEmpty(HELLO));
  }

  @Test
  public void test_nullOrEmpty_ExecutableFunction() {
    Strings.nullOrEmpty(null, () -> Assertions.assertTrue(true));
    Strings.nullOrEmpty("", () -> Assertions.assertTrue(true));
  }

  @Test
  public void test_nullOrEmpty_withDefaultValue() {
    Assertions.assertEquals(DEFAULT_VALUE, Strings.nullOrEmpty(null, DEFAULT_VALUE));
    Assertions.assertEquals(DEFAULT_VALUE, Strings.nullOrEmpty("", DEFAULT_VALUE));
    Assertions.assertEquals(HELLO, Strings.nullOrEmpty(HELLO, "Default"));
  }


  @Test
  public void test_nonNullNonEmpty() {
    Assertions.assertTrue(Strings.nonNullNonEmpty(HELLO));
    Assertions.assertFalse(Strings.nonNullNonEmpty(null));
    Assertions.assertFalse(Strings.nonNullNonEmpty(""));
  }

  @Test
  public void test_nonNullNonEmpty_ExecutableFunction() {
    Strings.nonNullNonEmpty(HELLO, () -> Assertions.assertTrue(true));
  }

  @Test
  public void test_nonNullNonEmpty_withDefaultValue() {
    Assertions.assertEquals(HELLO, Strings.nonNullNonEmpty(HELLO, DEFAULT_VALUE));
    Assertions.assertEquals(DEFAULT_VALUE, Strings.nonNullNonEmpty(null, DEFAULT_VALUE));
    Assertions.assertEquals(DEFAULT_VALUE, Strings.nonNullNonEmpty("", DEFAULT_VALUE));
  }

  @Test
  public void test_trimString() {
    String str = " Hello World! ";
    Assertions.assertEquals(HELLO, Strings.trimString(str));
  }

  @Test
  public void test_joinStrings() {
    String result = "Hello,World,!";
    Assertions.assertEquals(result, Strings.joinStrings(",", "Hello", "World", "!"));

    Assertions.assertNull(Strings.joinStrings(null, "Hello", "World", "!"));
    Assertions.assertEquals("", Strings.joinStrings(","));
  }

  @Test
  public void test_joinStringList() {
    String result = "Hello,World,!";
    Assertions.assertEquals(
        result, Strings.joinStringList(",", Arrays.asList("Hello", "World", "!")));

    Assertions.assertNull(Strings.joinStringList(null, Arrays.asList("Hello", "World", "!")));
    Assertions.assertEquals("", Strings.joinStringList(",", null));
  }

  @Test
  public void test_optBooleanFromString() {
    Assertions.assertTrue(Strings.optBooleanFromString("TRUE"));
    Assertions.assertTrue(Strings.optBooleanFromString("yes"));
    Assertions.assertFalse(Strings.optBooleanFromString(HELLO));
  }

  @Test
  public void test_removeNotSupportedASCIICharacters() {
    String str = "�ttrigeSomerzischeruchtanb";
    Assertions.assertEquals(
        "ttrigeSomerzischeruchtanb", Strings.removeNotSupportedASCIICharacters(str));

    Assertions.assertNull(Strings.removeNotSupportedASCIICharacters(null));
  }

  @Test
  public void test_removeNotSupportedUnicodeCharacters() {
    String str = "Thats a nice joke\uD83D\uDE06\uD83D\uDE06\uD83D\uDE06 \uD83D\uDE1B";
    Assertions.assertEquals("Thats a nice joke ", Strings.removeNotSupportedUnicodeCharacters(str));

    Assertions.assertNull(null, Strings.removeNotSupportedUnicodeCharacters(null));
  }

  //@Test
  public void test_normalize(){
    String str = "aeiöu";
    String normalizedString = "aeiöu";
    Assertions.assertEquals(normalizedString, Strings.normalize(str));
  }

  @Test
  public void test_hasDiacritics(){
    String str = "aeiöu";
    Assertions.assertTrue(Strings.hasDiacritics(str));
    Assertions.assertFalse(Strings.hasDiacritics(HELLO));
  }

  @Test
  public void test_equals(){
    Assertions.assertThrows(RuntimeException.class, () -> Strings.equals("A", "A", () -> {throw new RuntimeException("Function executed when two Strings are equal");}));
    Strings.equals("A", "a", () -> {throw new RuntimeException("Function won't execute when two Strings are equal with ignore case");});
    Strings.equals("A", "B", () -> {throw new RuntimeException("Function won't execute when two Strings are not equal");});
  }

  @Test
  public void test_notEquals(){
    Assertions.assertThrows(RuntimeException.class, () -> Strings.notEquals("A", "B", () -> {throw new RuntimeException("Function executed when two Strings are not equal");}));
    Strings.notEquals("A", "A", () -> {throw new RuntimeException("Function won't execute when two Strings are equal");});
  }

  @Test
  public void test_equalsIgnoreCase(){
    Assertions.assertTrue(Strings.equalsIgnoreCase("a", "A"));
    Assertions.assertTrue(Strings.equalsIgnoreCase("a", "a"));
    Assertions.assertTrue(Strings.equalsIgnoreCase(null, null));
    Assertions.assertFalse(Strings.equalsIgnoreCase("a", null));
    Assertions.assertFalse(Strings.equalsIgnoreCase(null, "b"));
  }

  @Test
  public void test_equalsIgnoreCaseWithExecutableFunction(){
    Assertions.assertThrows(RuntimeException.class, () -> Strings.equalsIgnoreCase("A", "A", () -> {throw new RuntimeException("Function executed when two Strings are equalsIgnoreCase");}));
    Assertions.assertThrows(RuntimeException.class, () -> Strings.equalsIgnoreCase("A", "a", () -> {throw new RuntimeException("Function executed when two Strings are equalsIgnoreCase");}));
    Strings.equalsIgnoreCase("A", "b", () -> {throw new RuntimeException("Function won't execute when two Strings are not equalsIgnoreCase");});
  }

  @Test
  public void test_notEqualsIgnoreCase(){
    Assertions.assertThrows(RuntimeException.class, () -> Strings.notEqualsIgnoreCase("A", "B", () -> {throw new RuntimeException("Function executed when two Strings are not equalsIgnoreCase");}));
    Assertions.assertThrows(RuntimeException.class, () -> Strings.notEqualsIgnoreCase("A", "b", () -> {throw new RuntimeException("Function executed when two Strings are not equalsIgnoreCase");}));
    Strings.notEqualsIgnoreCase("A", "a", () -> {throw new RuntimeException("Function won't execute when two Strings are equalsIgnoreCase");});
  }
}
