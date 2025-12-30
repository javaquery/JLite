package com.javaquery.poi.excel.writer;

import com.javaquery.annotations.Exportable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

/**
 * Writes data to an Excel file with customizable options.
 * @author vicky.thakor
 * @since 1.0.0
 * @param <T> the type of objects to be written
 */
public class ExcelWriter<T> {
    private List<String> headers;
    private List<String> keys;
    private Iterable<T> data;
    private boolean includeHeader = true;
    private File destination;

    private ExcelWriter() {}

    public static <T> ExcelWriter<T> builder() {
        return new ExcelWriter<>();
    }

    /**
     * Sets the headers for the Excel file.
     * @param headers the list of headers
     * @return the ExcelWriter instance
     */
    public ExcelWriter<T> headers(List<String> headers) {
        this.headers = headers;
        return this;
    }

    /**
     * Sets the keys corresponding to the data fields.
     * @param keys the list of keys
     * @return the ExcelWriter instance
     */
    public ExcelWriter<T> keys(List<String> keys) {
        this.keys = keys;
        return this;
    }

    /**
     * Sets the data to be written to the Excel file.
     * @param data the iterable data
     * @return the ExcelWriter instance
     */
    public ExcelWriter<T> data(Iterable<T> data) {
        this.data = data;
        return this;
    }

    /**
     * Sets the destination file for the Excel output.
     * @param destination the destination file
     * @return the ExcelWriter instance
     */
    public ExcelWriter<T> toFile(File destination) {
        this.destination = destination;
        return this;
    }

    /**
     * Sets whether to include the header row in the Excel file.
     * @param includeHeader true to include header, false otherwise
     * @return the ExcelWriter instance
     */
    public ExcelWriter<T> includeHeader(boolean includeHeader) {
        this.includeHeader = includeHeader;
        return this;
    }

    /**
     * Writes the data to the Excel file.
     * @throws IOException if an I/O error occurs
     */
    public void write() throws IOException {
        if (data == null || data.spliterator().getExactSizeIfKnown() <= 0) return;

        if (includeHeader && (headers == null || headers.isEmpty())) {
            throw new IllegalArgumentException("Headers must be provided when includeHeader is true");
        } else if (!includeHeader && (keys == null || keys.isEmpty())) {
            throw new IllegalArgumentException("Keys must be provided when includeHeader is false");
        } else if (includeHeader && headers.size() != keys.size()) {
            throw new IllegalArgumentException("Headers and Keys size must be same");
        }

        int numCols = keys.size();
        // to auto-size columns, we need to know the max width of each column
        int[] maxColWidths = new int[numCols];

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            Sheet sheet = workbook.createSheet("Sheet1");

            if (includeHeader) {
                // Write header
                Row headerRow = sheet.createRow(0);
                int headerCount = headers.size();
                for (int i = 0; i < headerCount; i++) {
                    String header = headers.get(i);
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(header);
                    maxColWidths[i] = header.length();
                }
            }

            int rowIdx = 1;
            for (T item : data) {
                int maxSubRows = 1;
                // Find max sublist size for this item
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
                    Row row = sheet.createRow(rowIdx++);
                    for (int colIdx = 0; colIdx < keysSize; colIdx++) {
                        String key = keys.get(colIdx);
                        Cell cell = row.createCell(colIdx);

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
                                CellValue cellValue = getNestedFieldValue(subItem, parts[1]);
                                setCellValue(cell, cellValue);
                            } else {
                                // Handle regular object field (not a collection)
                                CellValue cellValue = getNestedFieldValue(item, key);
                                setCellValue(cell, cellValue);
                            }
                        } else {
                            CellValue cellValue = getNestedFieldValue(item, key);
                            setCellValue(cell, cellValue);
                        }

                        // Track max column width
                        int cellValueLength = cell.getStringCellValue().length();
                        if (cellValueLength > maxColWidths[colIdx]) {
                            maxColWidths[colIdx] = cellValueLength;
                        }
                    }
                }
            }

            // Set column widths manually (approx. 256 units per character)
            for (int i = 0; i < numCols; i++) {
                sheet.setColumnWidth(i, (maxColWidths[i] + 2) * 256); // +2 for padding
            }
            // Write to file
            try (FileOutputStream fos = new FileOutputStream(destination.getAbsolutePath())) {
                workbook.write(fos);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the field of a class by its Exportable key.
     * @param clazz the class to search
     * @param fieldName the Exportable key
     * @return the Field object, or null if not found
     */
    private Field getField(Class<?> clazz, String fieldName) {
        for (Field field : clazz.getDeclaredFields()) {
            Exportable exp = field.getAnnotation(Exportable.class);
            if (exp != null && exp.key().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    /**
     * Gets the Exportable annotation of a field.
     * @param field the field to check
     * @return the Exportable annotation, or null if not present
     */
    private Exportable getExportableAnnotation(Field field) {
        return field.getAnnotation(Exportable.class);
    }

    /**
     * Recursively gets the value of a nested field using dot notation.
     * @param obj the object to retrieve the field from
     * @param keyPath the dot-notated key path
     * @return the field value, or null if not found
     */
    private CellValue getNestedFieldValue(Object obj, String keyPath) {
        try {
            String[] parts = keyPath.split("\\.", 2);
            Field field = getField(obj.getClass(), parts[0]);
            if (field == null) return null;
            field.setAccessible(true);
            Object value = field.get(obj);
            if (parts.length == 1) {
                return new CellValue(value, getExportableAnnotation(field));
            } else if (value != null) {
                return getNestedFieldValue(value, parts[1]);
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    /**
     * A helper class to hold cell value and its Exportable annotation.
     */
    @Getter
    private static class CellValue {
        private final Object value;
        private final Exportable exportable;

        public CellValue(Object value, Exportable exportable) {
            this.value = value;
            this.exportable = exportable;
        }
    }

    /**
     * Sets the value of a cell based on the CellValue object.
     * @param cell the cell to set the value for
     * @param cellValue the CellValue object containing the value and Exportable info
     */
    private void setCellValue(Cell cell, CellValue cellValue) {
        if (cellValue == null || cellValue.getValue() == null) {
            cell.setCellValue("");
            return;
        }

        Exportable exportable = cellValue.getExportable();
        Object value = cellValue.getValue();

        if (exportable != null && exportable.isFormula()) {
            cell.setCellFormula(value.toString());
        } else if (exportable != null && exportable.isRichText()) {
            RichTextString richText = new XSSFRichTextString(value.toString());
            cell.setCellValue(richText);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue((LocalDateTime) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }
}
