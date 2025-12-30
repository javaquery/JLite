package com.javaquery.poi.excel.reader;

import com.javaquery.helper.BatchProcessor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Reads an Excel file, transforms each row, and processes them in batches.
 * @author vicky.thakor
 * @since 1.0.0
 * @param <T> the type of objects to be processed
 */
public class ExcelReader<T> {
    private File source;
    private int sheetIndex = 0;
    private String sheetName;
    private int skipRows = 0;
    private ExcelRowTransformer<T> rowTransformer;
    private BatchProcessor<T> batchProcessor;
    private int batchSize = 1000;

    /**
     * Private constructor to enforce the use of the builder pattern.
     */
    private ExcelReader() {}

    /**
     * Creates a new ExcelReader instance using the builder pattern.
     * @param <T> the type of objects to be processed
     * @return a new ExcelReader instance
     */
    public static <T> ExcelReader<T> builder() {
        return new ExcelReader<>();
    }

    /**
     * Sets the source Excel file to read from.
     * @param source the source Excel file
     * @return the ExcelReader instance
     */
    public ExcelReader<T> source(File source) {
        this.source = source;
        return this;
    }

    /**
     * Sets the sheet index to read from (0-based).
     * @param sheetIndex the sheet index
     * @return the ExcelReader instance
     */
    public ExcelReader<T> sheetIndex(int sheetIndex) {
        if (sheetIndex < 0) {
            throw new IllegalArgumentException("Sheet index must be non-negative");
        }
        this.sheetIndex = sheetIndex;
        return this;
    }

    /**
     * Sets the sheet name to read from.
     * @param sheetName the sheet name
     * @return the ExcelReader instance
     */
    public ExcelReader<T> sheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    /**
     * Sets the number of rows to skip at the beginning of the sheet.
     * @param skipRows the number of rows to skip
     * @return the ExcelReader instance
     */
    public ExcelReader<T> skipRows(int skipRows) {
        if (skipRows < 0) {
            throw new IllegalArgumentException("Skip rows must be non-negative");
        }
        this.skipRows = skipRows;
        return this;
    }

    /**
     * Sets the row transformer to convert Excel rows into objects of type T.
     * @param rowTransformer the row transformer
     * @return the ExcelReader instance
     */
    public ExcelReader<T> rowTransformer(ExcelRowTransformer<T> rowTransformer) {
        this.rowTransformer = rowTransformer;
        return this;
    }

    /**
     * Sets the batch processor to handle batches of transformed objects.
     * @param batchProcessor the batch processor
     * @return the ExcelReader instance
     */
    public ExcelReader<T> batchProcessor(BatchProcessor<T> batchProcessor) {
        this.batchProcessor = batchProcessor;
        return this;
    }

    /**
     * Sets the size of each batch to be processed.
     * @param batchSize the batch size
     * @return the ExcelReader instance
     */
    public ExcelReader<T> batchSize(int batchSize) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("Batch size must be positive");
        }
        this.batchSize = batchSize;
        return this;
    }

    /**
     * Reads the Excel file, transforms rows, and processes them in batches.
     * @throws IOException if an I/O error occurs
     */
    public void read() throws IOException {
        validateInputs();

        // currentBatchCount is used to track number of items in current batch and avoid calling size() on list
        int currentBatchCount = 0, batchNumber = 0, totalProcessed = 0;
        Row headers = null;
        // Pre-allocate with exact capacity to avoid resizing
        List<T> transformedBatch = new ArrayList<>(batchSize);
        T previousRow = null;

        try (FileInputStream fis = new FileInputStream(source.getAbsoluteFile());
                Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = getSheet(workbook);
            Iterator<Row> rowIterator = sheet.iterator();

            // Skip rows if specified
            for (int i = 0; i < skipRows && rowIterator.hasNext(); i++) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (headers == null) {
                    headers = row;
                    continue;
                }

                T transformed = rowTransformer.transform(headers, row, previousRow);
                previousRow = transformed;
                if (transformed != null) {
                    transformedBatch.add(transformed);
                    currentBatchCount++;

                    // Process when batch is full
                    if (currentBatchCount >= batchSize) {
                        batchProcessor.onBatch(transformedBatch);
                        totalProcessed += currentBatchCount;
                        batchNumber++;

                        // Reset for next batch
                        transformedBatch.clear();
                        currentBatchCount = 0;
                    }
                }
                // Null out row reference to allow GC (helps with very large rows)
                row = null;
            }

            if (currentBatchCount > 0) {
                batchProcessor.onBatch(transformedBatch);
                totalProcessed += currentBatchCount;
                batchNumber++;
            }

            batchProcessor.onComplete(totalProcessed, batchNumber);
        }
    }

    private void validateInputs() {
        if (source == null) {
            throw new IllegalArgumentException("Source file must be provided");
        } else if (!source.exists()) {
            throw new IllegalArgumentException("Source file does not exist: " + source.getAbsolutePath());
        } else if (rowTransformer == null) {
            throw new IllegalArgumentException("Row transformer must be provided");
        } else if (batchProcessor == null) {
            throw new IllegalArgumentException("Batch processor must be provided");
        }
    }

    private Sheet getSheet(Workbook workbook) {
        if (sheetName != null && !sheetName.isEmpty()) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet with name '" + sheetName + "' not found");
            }
            return sheet;
        } else {
            if (sheetIndex >= workbook.getNumberOfSheets()) {
                throw new IllegalArgumentException("Sheet index " + sheetIndex + " is out of bounds. Workbook has "
                        + workbook.getNumberOfSheets() + " sheets");
            }
            return workbook.getSheetAt(sheetIndex);
        }
    }
}
