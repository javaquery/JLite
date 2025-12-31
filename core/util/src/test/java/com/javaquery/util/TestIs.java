package com.javaquery.util;

import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link Is}
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
public class TestIs {

    @Test
    public void test_isNull_withNull() {
        Assertions.assertTrue(Is.isNull(null));
    }

    @Test
    public void test_isNull_withNonNull() {
        Assertions.assertFalse(Is.isNull("test"));
        Assertions.assertFalse(Is.isNull(123));
        Assertions.assertFalse(Is.isNull(new Object()));
    }

    @Test
    public void test_isNull_withEmptyString() {
        Assertions.assertFalse(Is.isNull(""));
    }

    @Test
    public void test_isNull_withEmptyCollection() {
        Assertions.assertFalse(Is.isNull(new ArrayList<>()));
        Assertions.assertFalse(Is.isNull(new HashMap<>()));
    }

    @Test
    public void test_isNull_ExecutableFunction_withNull() {
        final boolean[] executed = {false};
        Is.isNull(null, () -> executed[0] = true);
        Assertions.assertTrue(executed[0]);
    }

    @Test
    public void test_isNull_ExecutableFunction_withNonNull() {
        final boolean[] executed = {false};
        Is.isNull("test", () -> executed[0] = true);
        Assertions.assertFalse(executed[0]);
    }

    @Test
    public void test_nonNull_withNull() {
        Assertions.assertFalse(Is.nonNull(null));
    }

    @Test
    public void test_nonNull_withNonNull() {
        Assertions.assertTrue(Is.nonNull("test"));
        Assertions.assertTrue(Is.nonNull(123));
        Assertions.assertTrue(Is.nonNull(new Object()));
    }

    @Test
    public void test_nonNull_withEmptyString() {
        Assertions.assertTrue(Is.nonNull(""));
    }

    @Test
    public void test_nonNull_withEmptyCollection() {
        Assertions.assertTrue(Is.nonNull(new ArrayList<>()));
        Assertions.assertTrue(Is.nonNull(new HashMap<>()));
    }

    @Test
    public void test_nonNull_ExecutableFunction_withNull() {
        final boolean[] executed = {false};
        Is.nonNull(null, () -> executed[0] = true);
        Assertions.assertFalse(executed[0]);
    }

    @Test
    public void test_nonNull_ExecutableFunction_withNonNull() {
        final boolean[] executed = {false};
        Is.nonNull("test", () -> executed[0] = true);
        Assertions.assertTrue(executed[0]);
    }

    @Test
    public void test_nullOrEmpty_String_withNull() {
        Assertions.assertTrue(Is.nullOrEmpty((String) null));
    }

    @Test
    public void test_nullOrEmpty_String_withEmpty() {
        Assertions.assertTrue(Is.nullOrEmpty(""));
    }

    @Test
    public void test_nullOrEmpty_String_withNonEmpty() {
        Assertions.assertFalse(Is.nullOrEmpty("test"));
    }

    @Test
    public void test_nullOrEmpty_String_withWhitespace() {
        Assertions.assertTrue(Is.nullOrEmpty(" "));
        Assertions.assertTrue(Is.nullOrEmpty("   "));
        Assertions.assertTrue(Is.nullOrEmpty("\t"));
        Assertions.assertTrue(Is.nullOrEmpty("\n"));
    }

    @Test
    public void test_nullOrEmpty_String_ExecutableFunction_withNull() {
        final boolean[] executed = {false};
        Is.nullOrEmpty((String) null, () -> executed[0] = true);
        Assertions.assertTrue(executed[0]);
    }

    @Test
    public void test_nullOrEmpty_String_ExecutableFunction_withEmpty() {
        final boolean[] executed = {false};
        Is.nullOrEmpty("", () -> executed[0] = true);
        Assertions.assertTrue(executed[0]);
    }

    @Test
    public void test_nullOrEmpty_String_ExecutableFunction_withNonEmpty() {
        final boolean[] executed = {false};
        Is.nullOrEmpty("test", () -> executed[0] = true);
        Assertions.assertFalse(executed[0]);
    }

    @Test
    public void test_nullOrEmpty_String_withDefaultValue_null() {
        Assertions.assertEquals("default", Is.nullOrEmpty(null, "default"));
    }

    @Test
    public void test_nullOrEmpty_String_withDefaultValue_empty() {
        Assertions.assertEquals("default", Is.nullOrEmpty("", "default"));
    }

    @Test
    public void test_nullOrEmpty_String_withDefaultValue_nonEmpty() {
        Assertions.assertEquals("test", Is.nullOrEmpty("test", "default"));
    }

    @Test
    public void test_nonNullNonEmpty_String_withNull() {
        Assertions.assertFalse(Is.nonNullNonEmpty((String) null));
    }

    @Test
    public void test_nonNullNonEmpty_String_withEmpty() {
        Assertions.assertFalse(Is.nonNullNonEmpty(""));
    }

    @Test
    public void test_nonNullNonEmpty_String_withNonEmpty() {
        Assertions.assertTrue(Is.nonNullNonEmpty("test"));
        Assertions.assertTrue(Is.nonNullNonEmpty("a"));
    }

    @Test
    public void test_nonNullNonEmpty_String_withWhitespace() {
        Assertions.assertFalse(Is.nonNullNonEmpty(" "));
        Assertions.assertFalse(Is.nonNullNonEmpty("   "));
        Assertions.assertFalse(Is.nonNullNonEmpty("\t"));
    }

    @Test
    public void test_nonNullNonEmpty_String_ExecutableFunction_withNull() {
        final boolean[] executed = {false};
        Is.nonNullNonEmpty((String) null, () -> executed[0] = true);
        Assertions.assertFalse(executed[0]);
    }

    @Test
    public void test_nonNullNonEmpty_String_ExecutableFunction_withEmpty() {
        final boolean[] executed = {false};
        Is.nonNullNonEmpty("", () -> executed[0] = true);
        Assertions.assertFalse(executed[0]);
    }

    @Test
    public void test_nonNullNonEmpty_String_ExecutableFunction_withNonEmpty() {
        final boolean[] executed = {false};
        Is.nonNullNonEmpty("test", () -> executed[0] = true);
        Assertions.assertTrue(executed[0]);
    }

    @Test
    public void test_nonNullNonEmpty_String_withDefaultValue_null() {
        Assertions.assertEquals("default", Is.nonNullNonEmpty(null, "default"));
    }

    @Test
    public void test_nonNullNonEmpty_String_withDefaultValue_empty() {
        Assertions.assertEquals("default", Is.nonNullNonEmpty("", "default"));
    }

    @Test
    public void test_nonNullNonEmpty_String_withDefaultValue_nonEmpty() {
        Assertions.assertEquals("test", Is.nonNullNonEmpty("test", "default"));
    }

    @Test
    public void test_nullOrEmpty_Collection_withNull() {
        Assertions.assertTrue(Is.nullOrEmpty((Collection<?>) null));
    }

    @Test
    public void test_nullOrEmpty_Collection_withEmpty() {
        Assertions.assertTrue(Is.nullOrEmpty(new ArrayList<>()));
        Assertions.assertTrue(Is.nullOrEmpty(new HashSet<>()));
    }

    @Test
    public void test_nullOrEmpty_Collection_withNonEmpty() {
        List<String> list = Arrays.asList("item1", "item2");
        Assertions.assertFalse(Is.nullOrEmpty(list));

        Set<Integer> set = new HashSet<>(Arrays.asList(1, 2, 3));
        Assertions.assertFalse(Is.nullOrEmpty(set));
    }

    @Test
    public void test_nullOrEmpty_Iterable_withNull() {
        Assertions.assertTrue(Is.nullOrEmpty((Iterable<?>) null));
    }

    @Test
    public void test_nullOrEmpty_Iterable_withEmpty() {
        Iterable<String> iterable = new ArrayList<>();
        Assertions.assertTrue(Is.nullOrEmpty(iterable));
    }

    @Test
    public void test_nullOrEmpty_Iterable_withNonEmpty() {
        Iterable<String> iterable = Arrays.asList("item1", "item2");
        Assertions.assertFalse(Is.nullOrEmpty(iterable));
    }

    @Test
    public void test_nullOrEmpty_Collection_ExecutableFunction_withNull() {
        final boolean[] executed = {false};
        Is.nullOrEmpty((Collection<?>) null, () -> executed[0] = true);
        Assertions.assertTrue(executed[0]);
    }

    @Test
    public void test_nullOrEmpty_Collection_ExecutableFunction_withEmpty() {
        final boolean[] executed = {false};
        Is.nullOrEmpty(new ArrayList<>(), () -> executed[0] = true);
        Assertions.assertTrue(executed[0]);
    }

    @Test
    public void test_nullOrEmpty_Collection_ExecutableFunction_withNonEmpty() {
        final boolean[] executed = {false};
        List<String> list = Arrays.asList("item1", "item2");
        Is.nullOrEmpty(list, () -> executed[0] = true);
        Assertions.assertFalse(executed[0]);
    }

    @Test
    public void test_nullOrEmpty_Iterable_ExecutableFunction_withNull() {
        final boolean[] executed = {false};
        Is.nullOrEmpty((Iterable<?>) null, () -> executed[0] = true);
        Assertions.assertTrue(executed[0]);
    }

    @Test
    public void test_nullOrEmpty_Iterable_ExecutableFunction_withEmpty() {
        final boolean[] executed = {false};
        Iterable<String> iterable = new ArrayList<>();
        Is.nullOrEmpty(iterable, () -> executed[0] = true);
        Assertions.assertTrue(executed[0]);
    }

    @Test
    public void test_nullOrEmpty_Iterable_ExecutableFunction_withNonEmpty() {
        final boolean[] executed = {false};
        Iterable<String> iterable = Arrays.asList("item1", "item2");
        Is.nullOrEmpty(iterable, () -> executed[0] = true);
        Assertions.assertFalse(executed[0]);
    }

    @Test
    public void test_nonNullNonEmpty_Collection_withNull() {
        Assertions.assertFalse(Is.nonNullNonEmpty((Collection<?>) null));
    }

    @Test
    public void test_nonNullNonEmpty_Collection_withEmpty() {
        Assertions.assertFalse(Is.nonNullNonEmpty(new ArrayList<>()));
        Assertions.assertFalse(Is.nonNullNonEmpty(new HashSet<>()));
    }

    @Test
    public void test_nonNullNonEmpty_Collection_withNonEmpty() {
        List<String> list = Arrays.asList("item1", "item2");
        Assertions.assertTrue(Is.nonNullNonEmpty(list));

        Set<Integer> set = new HashSet<>(Arrays.asList(1, 2, 3));
        Assertions.assertTrue(Is.nonNullNonEmpty(set));
    }

    @Test
    public void test_nonNullNonEmpty_Iterable_withNull() {
        Assertions.assertFalse(Is.nonNullNonEmpty((Iterable<?>) null));
    }

    @Test
    public void test_nonNullNonEmpty_Iterable_withEmpty() {
        Iterable<String> iterable = new ArrayList<>();
        Assertions.assertFalse(Is.nonNullNonEmpty(iterable));
    }

    @Test
    public void test_nonNullNonEmpty_Iterable_withNonEmpty() {
        Iterable<String> iterable = Arrays.asList("item1", "item2");
        Assertions.assertTrue(Is.nonNullNonEmpty(iterable));
    }

    @Test
    public void test_nonNullNonEmpty_Collection_ExecutableFunction_withNull() {
        final boolean[] executed = {false};
        Is.nonNullNonEmpty((Collection<?>) null, () -> executed[0] = true);
        Assertions.assertFalse(executed[0]);
    }

    @Test
    public void test_nonNullNonEmpty_Collection_ExecutableFunction_withEmpty() {
        final boolean[] executed = {false};
        Is.nonNullNonEmpty(new ArrayList<>(), () -> executed[0] = true);
        Assertions.assertFalse(executed[0]);
    }

    @Test
    public void test_nonNullNonEmpty_Collection_ExecutableFunction_withNonEmpty() {
        final boolean[] executed = {false};
        List<String> list = Arrays.asList("item1", "item2");
        Is.nonNullNonEmpty(list, () -> executed[0] = true);
        Assertions.assertTrue(executed[0]);
    }

    @Test
    public void test_nonNullNonEmpty_Iterable_ExecutableFunction_withNull() {
        final boolean[] executed = {false};
        Is.nonNullNonEmpty((Iterable<?>) null, () -> executed[0] = true);
        Assertions.assertFalse(executed[0]);
    }

    @Test
    public void test_nonNullNonEmpty_Iterable_ExecutableFunction_withEmpty() {
        final boolean[] executed = {false};
        Iterable<String> iterable = new ArrayList<>();
        Is.nonNullNonEmpty(iterable, () -> executed[0] = true);
        Assertions.assertFalse(executed[0]);
    }

    @Test
    public void test_nonNullNonEmpty_Iterable_ExecutableFunction_withNonEmpty() {
        final boolean[] executed = {false};
        Iterable<String> iterable = Arrays.asList("item1", "item2");
        Is.nonNullNonEmpty(iterable, () -> executed[0] = true);
        Assertions.assertTrue(executed[0]);
    }

    @Test
    public void test_nullOrEmpty_Map_withNull() {
        Assertions.assertTrue(Is.nullOrEmpty((Map<?, ?>) null));
    }

    @Test
    public void test_nullOrEmpty_Map_withEmpty() {
        Assertions.assertTrue(Is.nullOrEmpty(new HashMap<>()));
        Assertions.assertTrue(Is.nullOrEmpty(new TreeMap<>()));
    }

    @Test
    public void test_nullOrEmpty_Map_withNonEmpty() {
        Map<String, Integer> map = new HashMap<>();
        map.put("key1", 1);
        map.put("key2", 2);
        Assertions.assertFalse(Is.nullOrEmpty(map));
    }

    @Test
    public void test_nullOrEmpty_Map_ExecutableFunction_withNull() {
        final boolean[] executed = {false};
        Is.nullOrEmpty((Map<?, ?>) null, () -> executed[0] = true);
        Assertions.assertTrue(executed[0]);
    }

    @Test
    public void test_nullOrEmpty_Map_ExecutableFunction_withEmpty() {
        final boolean[] executed = {false};
        Is.nullOrEmpty(new HashMap<>(), () -> executed[0] = true);
        Assertions.assertTrue(executed[0]);
    }

    @Test
    public void test_nullOrEmpty_Map_ExecutableFunction_withNonEmpty() {
        final boolean[] executed = {false};
        Map<String, Integer> map = new HashMap<>();
        map.put("key1", 1);
        Is.nullOrEmpty(map, () -> executed[0] = true);
        Assertions.assertFalse(executed[0]);
    }

    @Test
    public void test_nonNullNonEmpty_Map_withNull() {
        Assertions.assertFalse(Is.nonNullNonEmpty((Map<?, ?>) null));
    }

    @Test
    public void test_nonNullNonEmpty_Map_withEmpty() {
        Assertions.assertFalse(Is.nonNullNonEmpty(new HashMap<>()));
        Assertions.assertFalse(Is.nonNullNonEmpty(new TreeMap<>()));
    }

    @Test
    public void test_nonNullNonEmpty_Map_withNonEmpty() {
        Map<String, Integer> map = new HashMap<>();
        map.put("key1", 1);
        map.put("key2", 2);
        Assertions.assertTrue(Is.nonNullNonEmpty(map));
    }

    @Test
    public void test_nonNullNonEmpty_Map_ExecutableFunction_withNull() {
        final boolean[] executed = {false};
        Is.nonNullNonEmpty((Map<?, ?>) null, () -> executed[0] = true);
        Assertions.assertFalse(executed[0]);
    }

    @Test
    public void test_nonNullNonEmpty_Map_ExecutableFunction_withEmpty() {
        final boolean[] executed = {false};
        Is.nonNullNonEmpty(new HashMap<>(), () -> executed[0] = true);
        Assertions.assertFalse(executed[0]);
    }

    @Test
    public void test_nonNullNonEmpty_Map_ExecutableFunction_withNonEmpty() {
        final boolean[] executed = {false};
        Map<String, Integer> map = new HashMap<>();
        map.put("key1", 1);
        Is.nonNullNonEmpty(map, () -> executed[0] = true);
        Assertions.assertTrue(executed[0]);
    }

    @Test
    public void test_edgeCase_whitespaceString() {
        Assertions.assertTrue(Is.nullOrEmpty(" "));
        Assertions.assertTrue(Is.nullOrEmpty("   "));
        Assertions.assertFalse(Is.nonNullNonEmpty(" "));
        Assertions.assertFalse(Is.nonNullNonEmpty("   "));
    }

    @Test
    public void test_edgeCase_singleElementCollection() {
        List<String> singleElement = Arrays.asList("item");
        Assertions.assertFalse(Is.nullOrEmpty(singleElement));
        Assertions.assertTrue(Is.nonNullNonEmpty(singleElement));
    }

    @Test
    public void test_edgeCase_singleEntryMap() {
        Map<String, String> singleEntry = new HashMap<>();
        singleEntry.put("key", "value");
        Assertions.assertFalse(Is.nullOrEmpty(singleEntry));
        Assertions.assertTrue(Is.nonNullNonEmpty(singleEntry));
    }

    @Test
    public void test_edgeCase_collectionWithNullElements() {
        List<String> listWithNulls = new ArrayList<>();
        listWithNulls.add(null);
        listWithNulls.add(null);

        Assertions.assertFalse(Is.nullOrEmpty(listWithNulls));
        Assertions.assertTrue(Is.nonNullNonEmpty(listWithNulls));
    }

    @Test
    public void test_edgeCase_mapWithNullValues() {
        Map<String, String> mapWithNullValues = new HashMap<>();
        mapWithNullValues.put("key1", null);
        mapWithNullValues.put("key2", null);

        Assertions.assertFalse(Is.nullOrEmpty(mapWithNullValues));
        Assertions.assertTrue(Is.nonNullNonEmpty(mapWithNullValues));
    }

    @Test
    public void test_multipleExecutions() {
        final int[] counter = {0};

        Is.isNull(null, () -> counter[0]++);
        Is.nonNull("test", () -> counter[0]++);
        Is.nullOrEmpty("", () -> counter[0]++);
        Is.nonNullNonEmpty("value", () -> counter[0]++);

        Assertions.assertEquals(4, counter[0]);
    }
}
