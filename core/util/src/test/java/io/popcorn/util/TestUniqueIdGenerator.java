package io.popcorn.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public class TestUniqueIdGenerator {

  @Test
  public void test_generate() {
    Assertions.assertNotNull(UniqueIdGenerator.generate());
  }
}
