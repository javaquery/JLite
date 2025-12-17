package com.javaquery.util.collection;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public class TestCollections {

    private static final List<String> EMPTY_LIST = new ArrayList<>(1);
    private static final List<String> NULL_LIST = null;
    private static final Iterable<String> EMPTY_ITERABLE = new ArrayList<>(1);
    private static final Iterable<String> NULL_ITERABLE = null;
    private static final Iterable<String> NON_EMPTY_ITERABLE = Collections.singletonList("dummy");
    private static final Set<String> EMPTY_SET = new HashSet<>(1);
    private static final Map<String, String> EMPTY_MAP = new HashMap<>(1);
    private static final Map<String, String> NULL_MAP = null;

    @Test
    public void test_singleton() {
        Set<String> singletonSet = Collections.singleton("dummy");
        Assertions.assertEquals(1, singletonSet.size());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> singletonSet.add("dummy2"));
    }

    @Test
    public void test_singletonList() {
        List<String> singletonList = Collections.singletonList("dummy");
        Assertions.assertEquals(1, singletonList.size());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> singletonList.add("dummy2"));
    }

    @Test
    public void test_singletonMap() {
        Map<String, Integer> singletonMap = Collections.singletonMap("counter", 100);
        Assertions.assertEquals(1, singletonMap.size());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> singletonMap.put("dummyCounter", 200));
    }

    @Test
    public void test_emptySet() {
        Set<Integer> emptySet = Collections.emptySet();
        Assertions.assertEquals(0, emptySet.size());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> emptySet.add(200));
    }

    @Test
    public void test_emptyList() {
        List<Integer> emptyList = Collections.emptyList();
        Assertions.assertEquals(0, emptyList.size());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> emptyList.add(200));
    }

    @Test
    public void test_emptyMap() {
        Map<Integer, String> emptyMap = Collections.emptyMap();
        Assertions.assertEquals(0, emptyMap.size());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> emptyMap.put(200, "dummy"));
    }

    @Test
    public void test_nullOrEmpty() {
        Assertions.assertTrue(Collections.nullOrEmpty(NULL_LIST));
        Assertions.assertTrue(Collections.nullOrEmpty(EMPTY_LIST));
        Assertions.assertFalse(Collections.nullOrEmpty(Collections.singletonList("A")));
        Assertions.assertTrue(Collections.nullOrEmpty(NULL_MAP));
        Assertions.assertTrue(Collections.nullOrEmpty(EMPTY_MAP));
        Assertions.assertTrue(Collections.nullOrEmpty(NULL_ITERABLE));
        Assertions.assertTrue(Collections.nullOrEmpty(EMPTY_ITERABLE));
        Assertions.assertFalse(Collections.nullOrEmpty(NON_EMPTY_ITERABLE));
    }

    @Test
    public void test_nullOrEmpty_ExecutableFunction() {
        Collections.nullOrEmpty(NULL_LIST, () -> Assertions.assertTrue(true));
        Collections.nullOrEmpty(EMPTY_ITERABLE, () -> Assertions.assertTrue(true));
        Collections.nullOrEmpty(NULL_MAP, () -> Assertions.assertTrue(true));
        Collections.nullOrEmpty(NON_EMPTY_ITERABLE, () -> {
            throw new AssertionError("Should not be called");
        });
        Collections.nullOrEmpty(List.of("A"), () -> {
            throw new AssertionError("Should not be called");
        });
        Collections.nullOrEmpty(Map.of("A", "B"), () -> {
            throw new AssertionError("Should not be called");
        });
    }

    @Test
    public void test_nullOrEmpty_2() {
        Assertions.assertFalse(Collections.nullOrEmpty(Collections.singletonMap("A", "B")));
    }

    @Test
    public void test_nonNullNotEmpty() {
        Assertions.assertTrue(Collections.nonNullNonEmpty(Collections.singletonList("A")));
        Assertions.assertTrue(Collections.nonNullNonEmpty(NON_EMPTY_ITERABLE));
        Assertions.assertFalse(Collections.nonNullNonEmpty(NULL_LIST));
        Assertions.assertFalse(Collections.nonNullNonEmpty(EMPTY_SET));
        Assertions.assertFalse(Collections.nonNullNonEmpty(NULL_ITERABLE));
        Assertions.assertFalse(Collections.nonNullNonEmpty(EMPTY_ITERABLE));
    }

    @Test
    public void test_nonNullNotEmpty_ExecutableFunction() {
        Collections.nonNullNonEmpty(Collections.singletonList("A"), () -> Assertions.assertTrue(true));
        Collections.nonNullNonEmpty(NON_EMPTY_ITERABLE, () -> Assertions.assertTrue(true));
        Collections.nonNullNonEmpty(NULL_LIST, () -> {
            throw new AssertionError("Should not be called");
        });
        Collections.nonNullNonEmpty(NULL_ITERABLE, () -> {
            throw new AssertionError("Should not be called");
        });
    }

    @Test
    public void test_notEmptyStream() {
        Collections.notEmpty(Collections.singletonList("A")).forEach(Assertions::assertNotNull);
    }

    @Test
    public void test_notEmptyStream_emptyCollection() {
        Stream<?> stream = Collections.notEmpty(EMPTY_LIST);
        Assertions.assertEquals(0, stream.count());
    }

    @Test
    public void test_nonNullNotEmptyMap() {
        Assertions.assertTrue(Collections.nonNullNonEmpty(Collections.singletonMap("A", "B")));

        Assertions.assertFalse(Collections.nonNullNonEmpty(NULL_MAP));
        Assertions.assertFalse(Collections.nonNullNonEmpty(EMPTY_MAP));
    }

    @Test
    public void test_nonNullNotEmptyMap_ExecutableFunction() {
        Collections.nonNullNonEmpty(Collections.singletonMap("A", "B"), () -> Assertions.assertTrue(true));
        Collections.nonNullNonEmpty(NULL_MAP, () -> {
            throw new AssertionError("Should not be called");
        });
    }

    @Test
    public void test_batches_1() {
        List<String> listABCD = new ArrayList<>();
        listABCD.add("A");
        listABCD.add("B");
        listABCD.add("C");
        listABCD.add("D");
        listABCD.add("E");

        AtomicInteger batchCounter = new AtomicInteger(0);
        List<String> result = new ArrayList<>(5);
        Collections.batches(listABCD, 2).forEach(listOfString -> {
            batchCounter.getAndIncrement();
            result.addAll(listOfString);
        });
        Assertions.assertEquals(3, batchCounter.get());
        Assertions.assertEquals(5, result.size());
        Assertions.assertTrue(result.contains("E"));
    }

    @Test
    public void test_batches_2() {
        List<String> listABCD = new ArrayList<>();
        listABCD.add("A");
        listABCD.add("B");

        AtomicInteger batchCounter = new AtomicInteger(0);
        List<String> result = new ArrayList<>(2);
        Collections.batches(listABCD, 0).forEach(listOfString -> {
            batchCounter.getAndIncrement();
            result.addAll(listOfString);
        });
        Assertions.assertEquals(1, batchCounter.get());
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains("A"));
    }

    @Test
    public void test_batches_3() {
        Stream<List<String>> stringStream = Collections.batches(EMPTY_LIST, 1);
        Assertions.assertEquals(0, stringStream.count());

        Assertions.assertThrows(IllegalArgumentException.class, () -> Collections.batches(null, 2));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Collections.batches(EMPTY_LIST, -2));
    }

    @Test
    public void test_isCollectionEqual() {
        List<String> listOne = new ArrayList<>();
        listOne.add("apple");

        List<String> listTwo = new ArrayList<>();
        listTwo.add("apple");

        Assertions.assertTrue(Collections.isCollectionEqual(listOne, listTwo));
    }

    @Test
    public void test_isCollectionEqual_1() {
        Set<String> setOne = new HashSet<>();
        setOne.add("apple");
        setOne.add("orange");

        Set<String> setTwo = new HashSet<>();
        setTwo.add("apple");

        Assertions.assertFalse(Collections.isCollectionEqual(setOne, setTwo));
    }

    @Test
    public void test_isCollectionEqual_2() {
        Set<String> setOne = new HashSet<>();
        setOne.add("orange");

        Set<String> setTwo = new HashSet<>();
        setTwo.add("apple");

        Assertions.assertFalse(Collections.isCollectionEqual(setOne, setTwo));
    }

    @Test
    public void test_isCollectionEqual_3() {
        List<String> setOne = new ArrayList<>();
        setOne.add("orange");
        setOne.add("orange");

        Set<String> setTwo = new HashSet<>();
        setTwo.add(null);
        setTwo.add("apple");

        Assertions.assertFalse(Collections.isCollectionEqual(setOne, setTwo));
    }
}
