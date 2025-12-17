package com.javaquery.util.collection;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * @author vicky.thakor
 * @since 1.2.8
 */
public class TestArrays {

    @Test
    void testToArrayWithStringList() {
        // Given
        List<String> list = new ArrayList<>();
        list.add("apple");
        list.add("banana");
        list.add("cherry");

        // When
        String[] result = Arrays.toArray(list, new String[0]);

        // Then
        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals("apple", result[0]);
        assertEquals("banana", result[1]);
        assertEquals("cherry", result[2]);
    }

    @Test
    void testToArrayWithIntegerList() {
        // Given
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);

        // When
        Integer[] result = Arrays.toArray(list, new Integer[0]);

        // Then
        assertNotNull(result);
        assertEquals(5, result.length);
        assertEquals(1, result[0]);
        assertEquals(2, result[1]);
        assertEquals(3, result[2]);
        assertEquals(4, result[3]);
        assertEquals(5, result[4]);
    }

    @Test
    void testToArrayWithEmptyList() {
        // Given
        List<String> list = new ArrayList<>();

        // When
        String[] result = Arrays.toArray(list, new String[0]);

        // Then
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void testToArrayWithNullInput() {
        // Given
        Iterable<String> iterable = null;

        // When
        String[] result = Arrays.toArray(iterable, new String[0]);

        // Then
        assertNull(result);
    }

    @Test
    void testToArrayWithSet() {
        // Given
        Set<String> set = new HashSet<>();
        set.add("one");
        set.add("two");
        set.add("three");

        // When
        String[] result = Arrays.toArray(set, new String[0]);

        // Then
        assertNotNull(result);
        assertEquals(3, result.length);
        assertTrue(java.util.Arrays.asList(result).contains("one"));
        assertTrue(java.util.Arrays.asList(result).contains("two"));
        assertTrue(java.util.Arrays.asList(result).contains("three"));
    }

    @Test
    void testToArrayWithSingleElement() {
        // Given
        List<Double> list = new ArrayList<>();
        list.add(3.14);

        // When
        Double[] result = Arrays.toArray(list, new Double[0]);

        // Then
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(3.14, result[0]);
    }

    @Test
    void testToArrayWithNullElements() {
        // Given
        List<String> list = new ArrayList<>();
        list.add("first");
        list.add(null);
        list.add("third");

        // When
        String[] result = Arrays.toArray(list, new String[0]);

        // Then
        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals("first", result[0]);
        assertNull(result[1]);
        assertEquals("third", result[2]);
    }

    @Test
    void testToArrayWithPreSizedPrototype() {
        // Given
        List<String> list = new ArrayList<>();
        list.add("alpha");
        list.add("beta");

        // When
        String[] result = Arrays.toArray(list, new String[2]);

        // Then
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals("alpha", result[0]);
        assertEquals("beta", result[1]);
    }

    @Test
    void testToArrayWithCustomObjects() {
        // Given
        List<Person> list = new ArrayList<>();
        list.add(new Person("John", 30));
        list.add(new Person("Jane", 25));

        // When
        Person[] result = Arrays.toArray(list, new Person[0]);

        // Then
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals("John", result[0].name);
        assertEquals(30, result[0].age);
        assertEquals("Jane", result[1].name);
        assertEquals(25, result[1].age);
    }

    // Helper class for testing custom objects
    private static class Person {
        String name;
        int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
}
