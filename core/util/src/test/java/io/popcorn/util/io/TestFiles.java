package io.popcorn.util.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.UUID;

/**
 * @author vicky.thakor
 * @since 1.0
 */
public class TestFiles {

  private static final String TEMP_DATA = "Hello World";
  private static final String INVALID_PATH = "T2:" + File.separatorChar + "javaquery.txt";
  private static final String DEMO_JSON_DATA = "{\"name\": \"vicky thakor\"}";

  @Test
  public void test_createNewFile() {
    File file = new File(getTemporaryFilePath());
    Assertions.assertTrue(Files.createNewFile(file));
    file.delete();
  }

  @Test
  public void test_createNewFile_1() {
    Assertions.assertFalse(Files.createNewFile(new File(INVALID_PATH)));
  }

  @Test
  public void test_createNewFile_2() {
    Assertions.assertThrows(NullPointerException.class, () -> Files.createNewFile(null));
  }

  @Test
  public void test_deleteIfExists_success() {
    File file = new File(getTemporaryFilePath());
    Files.createNewFile(file);
    Assertions.assertTrue(Files.deleteIfExists(file));
  }

  @Test
  public void test_deleteIfExists_failure() {
    File file = new File(getTemporaryFilePath());
    Assertions.assertFalse(Files.deleteIfExists(file));
  }

  @Test
  public void test_deleteAndCreateNewFile() {
    File file = new File(getTemporaryFilePath());
    Files.createNewFile(file);
    Assertions.assertTrue(Files.deleteAndCreateNewFile(file));
    file.delete();
  }

  @Test
  public void test_deleteAndCreateNewFile_1() {
    Assertions.assertThrows(NullPointerException.class, () -> Files.deleteAndCreateNewFile(null));
  }

  @Test
  public void test_writeToFile() {
    File file = new File(getTemporaryFilePath());
    Files.writeToFile(file, TEMP_DATA);
    String result = Files.readFromFile(file);
    Assertions.assertEquals(TEMP_DATA, result);
    file.delete();
  }

  @Test
  public void test_writeToFile_1() {
    // Silent exception logged and for code coverage
    Files.writeToFile(new File(INVALID_PATH), TEMP_DATA);
  }

  @Test
  public void test_writeToFile_2() {
    Assertions.assertThrows(NullPointerException.class, () -> Files.writeToFile(null, TEMP_DATA));
    Assertions.assertThrows(
        NullPointerException.class,
        () -> Files.writeToFile(new File(getTemporaryFilePath()), null));
  }

  @Test
  public void test_appendToFile() {
    File file = new File(getTemporaryFilePath());
    Files.writeToFile(file, "Hello");
    Files.appendToFile(file, " World", false);
    String result = Files.readFromFile(file);
    Assertions.assertEquals(TEMP_DATA, result);
    file.delete();
  }

  @Test
  public void test_appendToFile_1() {
    File file = new File(getTemporaryFilePath());
    Files.appendToFile(file, "Hello", true);
    Files.appendToFile(file, "World", false);
    String result = Files.readFromFile(file);
    Assertions.assertEquals("Hello\nWorld", result);
    file.delete();
  }

  @Test
  public void test_appendToFile_2() {
    // Silent exception logged and for code coverage
    Files.appendToFile(new File(INVALID_PATH), TEMP_DATA, false);
  }

  @Test
  public void test_appendToFile_3() {
    Assertions.assertThrows(
        NullPointerException.class, () -> Files.appendToFile(null, TEMP_DATA, false));
    Assertions.assertThrows(
        NullPointerException.class,
        () -> Files.appendToFile(new File(getTemporaryFilePath()), null, false));
  }

  @Test
  public void test_readFromFile() {
    File file = new File(getTemporaryFilePath());
    Files.writeToFile(file, TEMP_DATA);
    String result = Files.readFromFile(file);
    Assertions.assertEquals(TEMP_DATA, result);
    file.delete();
  }

  @Test
  public void test_readFromFile_1() {
    File file = new File(INVALID_PATH);
    if(file.exists()){
      file.delete();
    }
    // Silent exception logged and for code coverage
    String result = Files.readFromFile(file);
    Assertions.assertNull(result);
  }

  @Test
  public void test_readFromFile_2() {
    Assertions.assertThrows(NullPointerException.class, () -> Files.readFromFile(null));
  }

  @Test
  public void test_loadResource() {
    String result = new Files().loadResource("/sample/demo.json");
    Assertions.assertEquals(DEMO_JSON_DATA, result);
  }

  @Test
  public void test_loadResource_1() {
    Assertions.assertNull(new Files().loadResource("file-not-exist.json"));
  }

  @Test
  public void test_loadResource_2() {
    Assertions.assertThrows(NullPointerException.class, () -> new Files().loadResource(null));
  }

  @Test
  public void test_loadResource_3() {
    Assertions.assertNull(new Files().loadResource(" "));
  }

  private String getTemporaryFilePath() {
    return Files.SYSTEM_TMP_DIR
        + File.separatorChar
        + "javaquery"
        + File.separatorChar
        + "javaquery_"
        + UUID.randomUUID().toString()
        + ".txt";
  }
}
