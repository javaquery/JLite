package com.javaquery.opencsv.reader;

/**
 * @author vicky.thakor
 * @since 2025-12-09
 */
public interface CsvRowTransformer<T> {
    T transform(String[] headers, String[] rowValues, T previousRow);
}
