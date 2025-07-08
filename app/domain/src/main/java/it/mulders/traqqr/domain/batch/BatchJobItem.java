package it.mulders.traqqr.domain.batch;

/**
 * Marks the processing of an item during a batch job.
 * @param batchJob The batch job that processed the item.
 * @param status The status of the processing.
 * @param item The domain entity that was processed.
 * @param <T> The type of the domain entity that was processed.
 */
public record BatchJobItem<T>(BatchJob batchJob, BatchJobItemStatus status, T item) {}
