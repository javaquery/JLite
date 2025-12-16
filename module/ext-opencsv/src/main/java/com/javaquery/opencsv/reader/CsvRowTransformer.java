package com.javaquery.opencsv.reader;

/**
 * Transforms a CSV row into an object of type T.
 * @author vicky.thakor
 * @since 1.0.0
 * @param <T> the type of object to transform to
 */
public interface CsvRowTransformer<T> {
    /**
     * Transforms a CSV row into an object of type T.
     * @param headers the CSV headers
     * @param rowValues the values of the current row
     * @param previousRow the previously transformed row, or null if this is the first row
     * @return the transformed object of type T
     */
    T transform(String[] headers, String[] rowValues, T previousRow);
}
