package com.javaquery.opencsv.writer;

import com.javaquery.Exportable;
import com.opencsv.CSVWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

/**
 * @author vicky.thakor
 * @since 2025-12-09
 */
public class CsvWriter<T> {
    private List<String> headers;
    private List<String> keys;
    private Iterable<T> data;
    private File destination;
    private char delimiter = ',';
    private char quotechar = '"';
    private char escapechar = '"';
    private String lineEnd = "\n";
    private boolean includeHeader = true;

    private CsvWriter() {}

    public static <T> CsvWriter<T> builder() {
        return new CsvWriter<>();
    }

    public CsvWriter<T> headers(List<String> headers) {
        this.headers = headers;
        return this;
    }

    public CsvWriter<T> keys(List<String> keys) {
        this.keys = keys;
        return this;
    }

    public CsvWriter<T> data(Iterable<T> data) {
        this.data = data;
        return this;
    }

    public CsvWriter<T> toFile(File destination) {
        this.destination = destination;
        return this;
    }

    public CsvWriter<T> delimiter(char delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public CsvWriter<T> quoteChar(char quotechar) {
        this.quotechar = quotechar;
        return this;
    }

    public CsvWriter<T> escapeChar(char escapechar) {
        this.escapechar = escapechar;
        return this;
    }

    public CsvWriter<T> lineEnd(String lineEnd) {
        this.lineEnd = lineEnd;
        return this;
    }

    public CsvWriter<T> includeHeader(boolean includeHeader) {
        this.includeHeader = includeHeader;
        return this;
    }

    public void write() throws IOException {
        if (data == null || data.spliterator().getExactSizeIfKnown() <= 0) return;

        if (includeHeader && (headers == null || headers.isEmpty())) {
            throw new IllegalArgumentException("Headers must be provided when includeHeader is true");
        } else if (!includeHeader && (keys == null || keys.isEmpty())) {
            throw new IllegalArgumentException("Keys must be provided when includeHeader is false");
        } else if (includeHeader && headers.size() != keys.size()) {
            throw new IllegalArgumentException("Headers and Keys size must be same");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(destination));
                CSVWriter writer = new CSVWriter(bw, delimiter, quotechar, escapechar, lineEnd)) {
            if (includeHeader) {
                writer.writeNext(headers.toArray(new String[0]));
            }

            for (T item : data) {
                int maxSubRows = 1;
                for (String key : keys) {
                    if (key.contains(".")) {
                        String[] parts = key.split("\\.", 2);
                        Field field = getField(item.getClass(), parts[0]);
                        if (field != null
                                && (List.class.isAssignableFrom(field.getType())
                                        || Set.class.isAssignableFrom(field.getType()))) {
                            field.setAccessible(true);
                            Object collectionObj = field.get(item);
                            int size = 0;
                            if (collectionObj instanceof List) {
                                size = ((List<?>) collectionObj).size();
                            } else if (collectionObj instanceof Set) {
                                size = ((Set<?>) collectionObj).size();
                            }
                            if (size > maxSubRows) {
                                maxSubRows = size;
                            }
                        }
                    }
                }
                int keysSize = keys.size();
                for (int subRow = 0; subRow < maxSubRows; subRow++) {
                    String[] row = new String[keysSize];
                    for (int colIdx = 0; colIdx < keysSize; colIdx++) {
                        String key = keys.get(colIdx);
                        if (key.contains(".")) {
                            String[] parts = key.split("\\.", 2);
                            Field field = getField(item.getClass(), parts[0]);
                            if (field != null
                                    && (List.class.isAssignableFrom(field.getType())
                                            || Set.class.isAssignableFrom(field.getType()))) {
                                field.setAccessible(true);
                                Object collectionObj = field.get(item);
                                Object subItem = null;
                                if (collectionObj instanceof List<?>) {
                                    List<?> subList = (List<?>) collectionObj;
                                    if (subRow < subList.size()) {
                                        subItem = subList.get(subRow);
                                    }
                                } else if (collectionObj instanceof Set<?>) {
                                    Set<?> subSet = (Set<?>) collectionObj;
                                    if (subRow < subSet.size()) {
                                        subItem = subSet.stream()
                                                .skip(subRow)
                                                .findFirst()
                                                .orElse(null);
                                    }
                                }
                                Object value = getNestedFieldValue(subItem, parts[1]);
                                row[colIdx] = value != null ? value.toString() : "";
                            } else {
                                // Handle regular object field (not a collection)
                                Object value = getNestedFieldValue(item, key);
                                row[colIdx] = value != null ? value.toString() : "";
                            }
                        } else {
                            Object value = getNestedFieldValue(item, key);
                            row[colIdx] = value != null ? value.toString() : "";
                        }
                    }
                    writer.writeNext(row);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Field getField(Class<?> clazz, String fieldName) {
        for (Field field : clazz.getDeclaredFields()) {
            Exportable exp = field.getAnnotation(Exportable.class);
            if (exp != null && exp.key().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    private Object getNestedFieldValue(Object obj, String keyPath) {
        try {
            String[] parts = keyPath.split("\\.", 2);
            Field field = getField(obj.getClass(), parts[0]);
            if (field == null) return null;
            field.setAccessible(true);
            Object value = field.get(obj);
            if (parts.length == 1) {
                return value;
            } else if (value != null) {
                return getNestedFieldValue(value, parts[1]);
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }
}
