package io.popcorn.util;

import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestAssert {

    private static final RuntimeException RUNTIME_EXCEPTION = new RuntimeException("exception");
    private static final Object NULL_OBJECT = null;
    private static final Object NON_NULL_OBJECT = new Object();
    private static final Collection<Integer> EMPTY_COLLECTION = new ArrayList<>(0);
    private static final Collection<Integer> NON_EMPTY_COLLECTION = Collections.singletonList(1);
    private static final Map<Integer, String> EMPTY_MAP = new HashMap<>(0);
    private static final Map<Integer, String> NON_EMPTY_MAP = Collections.singletonMap(1, "A");

    private static RuntimeException getRuntimeException() {
        return RUNTIME_EXCEPTION;
    }

    @Test
    void test_nonNull() {
        Assertions.assertDoesNotThrow(() -> Assert.nonNull(NON_NULL_OBJECT, TestAssert::getRuntimeException));
        Assertions.assertThrows(
                RuntimeException.class, () -> Assert.nonNull(NULL_OBJECT, TestAssert::getRuntimeException));
        Assertions.assertThrows(NullPointerException.class, () -> Assert.nonNull(NULL_OBJECT, null));
    }

    @Test
    void test_isNull() {
        Assertions.assertDoesNotThrow(() -> Assert.isNull(NULL_OBJECT, TestAssert::getRuntimeException));
        Assertions.assertThrows(
                RuntimeException.class, () -> Assert.isNull(NON_NULL_OBJECT, TestAssert::getRuntimeException));
        Assertions.assertThrows(NullPointerException.class, () -> Assert.isNull(NON_NULL_OBJECT, null));
    }

    @Test
    void test_isTrue() {
        Assertions.assertDoesNotThrow(() -> Assert.isTrue(true, TestAssert::getRuntimeException));
        Assertions.assertThrows(RuntimeException.class, () -> Assert.isTrue(false, TestAssert::getRuntimeException));
        Assertions.assertThrows(NullPointerException.class, () -> Assert.isTrue(false, null));
    }

    @Test
    void test_isFalse() {
        Assertions.assertDoesNotThrow(() -> Assert.isFalse(false, TestAssert::getRuntimeException));
        Assertions.assertThrows(RuntimeException.class, () -> Assert.isFalse(true, TestAssert::getRuntimeException));
        Assertions.assertThrows(NullPointerException.class, () -> Assert.isFalse(true, null));
    }

    @Test
    void test_nonNullNonEmpty() {
        Assertions.assertDoesNotThrow(
                () -> Assert.nonNullNonEmpty(NON_EMPTY_COLLECTION, TestAssert::getRuntimeException));
        Assertions.assertThrows(
                RuntimeException.class, () -> Assert.nonNullNonEmpty(null, TestAssert::getRuntimeException));
        Assertions.assertThrows(NullPointerException.class, () -> Assert.nonNullNonEmpty(null, null));
        Assertions.assertThrows(
                RuntimeException.class,
                () -> Assert.nonNullNonEmpty(EMPTY_COLLECTION, TestAssert::getRuntimeException));
        Assertions.assertThrows(NullPointerException.class, () -> Assert.nonNullNonEmpty(EMPTY_COLLECTION, null));
    }

    @Test
    void test_nullOrEmpty() {
        Assertions.assertDoesNotThrow(() -> Assert.nullOrEmpty(null, TestAssert::getRuntimeException));
        Assertions.assertDoesNotThrow(() -> Assert.nullOrEmpty(EMPTY_COLLECTION, TestAssert::getRuntimeException));
        Assertions.assertThrows(
                RuntimeException.class,
                () -> Assert.nullOrEmpty(NON_EMPTY_COLLECTION, TestAssert::getRuntimeException));
        Assertions.assertThrows(NullPointerException.class, () -> Assert.nullOrEmpty(NON_EMPTY_COLLECTION, null));
    }

    @Test
    void test_nonNullNonEmptyMap() {
        Assertions.assertDoesNotThrow(() -> Assert.nonNullNonEmptyMap(NON_EMPTY_MAP, TestAssert::getRuntimeException));
        Assertions.assertThrows(
                RuntimeException.class, () -> Assert.nonNullNonEmptyMap(null, TestAssert::getRuntimeException));
        Assertions.assertThrows(NullPointerException.class, () -> Assert.nonNullNonEmptyMap(null, null));
        Assertions.assertThrows(
                RuntimeException.class, () -> Assert.nonNullNonEmptyMap(EMPTY_MAP, TestAssert::getRuntimeException));
        Assertions.assertThrows(NullPointerException.class, () -> Assert.nonNullNonEmptyMap(EMPTY_MAP, null));
    }

    @Test
    void test_nullOrEmptyMap() {
        Assertions.assertDoesNotThrow(() -> Assert.nullOrEmptyMap(null, TestAssert::getRuntimeException));
        Assertions.assertDoesNotThrow(() -> Assert.nullOrEmptyMap(EMPTY_MAP, TestAssert::getRuntimeException));
        Assertions.assertThrows(
                RuntimeException.class, () -> Assert.nullOrEmptyMap(NON_EMPTY_MAP, TestAssert::getRuntimeException));
        Assertions.assertThrows(NullPointerException.class, () -> Assert.nullOrEmptyMap(NON_EMPTY_MAP, null));
    }
}
