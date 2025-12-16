package com.javaquery.opencsv.reader;

import java.util.List;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public interface BatchProcessor<E> {
    void onBatch(List<E> batch);

    default void onComplete(int totalProcessed, int totalBatches) {
        // default implementation does nothing
    }
}
