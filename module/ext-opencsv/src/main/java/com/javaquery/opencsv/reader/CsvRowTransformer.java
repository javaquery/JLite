package com.javaquery.opencsv.reader;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public interface CsvRowTransformer<T> {
    T transform(String[] headers, String[] rowValues, T previousRow);
}
