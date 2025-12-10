package com.javaquery.opencsv.reader;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public class CsvReader<T> {
    private File source;
    private CsvRowTransformer<T> rowTransformer;
    private BatchProcessor<T> batchProcessor;
    private int batchSize = 1000;

    public static <T> CsvReader<T> builder() {
        return new CsvReader<>();
    }

    public CsvReader<T> source(File source) {
        this.source = source;
        return this;
    }

    public CsvReader<T> rowTransformer(CsvRowTransformer<T> rowTransformer) {
        this.rowTransformer = rowTransformer;
        return this;
    }

    public CsvReader<T> batchProcessor(BatchProcessor<T> batchProcessor) {
        this.batchProcessor = batchProcessor;
        return this;
    }

    public CsvReader<T> batchSize(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public void read() throws IOException {
        validateInputs();

        // currentBatchCount is used to track number of items in current batch and avoid calling size() on list
        int currentBatchCount = 0, batchNumber = 0, totalProcessed = 0;
        String[] headers = null;
        // Pre-allocate with exact capacity to avoid resizing
        List<T> transformedBatch = new ArrayList<>(batchSize);

        try (BufferedReader br = new BufferedReader(new FileReader(source.getAbsoluteFile()));
                CSVReader reader = new CSVReader(br)) {

            String[] line;
            while ((line = reader.readNext()) != null) {
                if (headers == null) {
                    headers = line;
                    continue;
                }

                T transformed = rowTransformer.transform(headers, line);
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
                // Null out line reference to allow GC (helps with very large rows)
                line = null;
            }

            if (currentBatchCount > 0) {
                batchProcessor.onBatch(transformedBatch);
                totalProcessed += currentBatchCount;
                batchNumber++;
            }

            batchProcessor.onComplete(totalProcessed, batchNumber);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateInputs() {
        if (source == null) {
            throw new IllegalArgumentException("Source file must be provided");
        } else if (rowTransformer == null) {
            throw new IllegalArgumentException("Row transformer must be provided");
        } else if (batchProcessor == null) {
            throw new IllegalArgumentException("Batch processor must be provided");
        }
    }
}
