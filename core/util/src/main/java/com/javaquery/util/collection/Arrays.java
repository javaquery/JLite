package com.javaquery.util.collection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vicky.thakor
 * @since 1.2.8
 */
public class Arrays {

    /**
     * Converts an Iterable to an Array of the specified prototype type.
     *
     * @param iterable the Iterable to convert
     * @param prototype an array of the desired type to determine the runtime type of the returned array
     * @param <T> the type of elements in the Iterable and the returned array
     * @return an array containing all elements from the Iterable, or null if the input is null
     */
    public static <T> T[] toArray(Iterable<T> iterable, T[] prototype) {
        if (iterable == null) {
            return null;
        }
        List<T> list = new ArrayList<>();
        for (T item : iterable) {
            list.add(item);
        }
        return list.toArray(prototype);
    }
}
