package com.javaquery.poi.excel.reader;

import org.apache.poi.ss.usermodel.Row;

/**
 * Transforms an Excel row into an object of type T.
 * @author vicky.thakor
 * @since 1.0.0
 * @param <T> the type of object to transform to
 */
public interface ExcelRowTransformer<T> {
    /**
     * Transforms an Excel row into an object of type T.
     * @param headers the header row
     * @param currentRow the current data row
     * @param previousRow the previously transformed row, or null if this is the first row
     * @return the transformed object of type T
     */
    T transform(Row headers, Row currentRow, T previousRow);
}
