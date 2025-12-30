package com.javaquery.helper;

import java.util.List;

/**
 * Batch processor interface for handling batches of records.
 * @author vicky.thakor
 * @since 1.2.8
 * @param <E> the type of records to be processed
 */
public interface BatchProcessor<E> {

    /**
     * Process a batch of records.
     * @param batch the list of records in the current batch
     */
    void onBatch(List<E> batch);

    /**
     * Called when all batches have been processed.
     * @param totalProcessed the total number of records processed
     * @param totalBatches the total number of batches processed
     */
    default void onComplete(int totalProcessed, int totalBatches) {
        // default implementation does nothing
    }
}
