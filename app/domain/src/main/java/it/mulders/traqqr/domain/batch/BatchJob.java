package it.mulders.traqqr.domain.batch;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class BatchJob {
    private final UUID id = UUID.randomUUID();
    private final OffsetDateTime started;
    private final OffsetDateTime ended;
    private final OffsetDateTime lastUpdated;
    private final BatchJobType type;
    private final BatchJobStatus status;
    private final Map<BatchJobItemStatus, Long> itemsProcessedByStatus;
    private final Long instanceId;
    private final Long executionId;

    public BatchJob(
            OffsetDateTime started,
            OffsetDateTime ended,
            OffsetDateTime lastUpdated,
            BatchJobType type,
            BatchJobStatus status,
            Map<BatchJobItemStatus, Long> itemsProcessedByStatus,
            Long instanceId,
            Long executionId) {
        this.started = started;
        this.ended = ended;
        this.lastUpdated = lastUpdated;
        this.type = type;
        this.status = status;
        this.itemsProcessedByStatus = itemsProcessedByStatus;
        this.instanceId = instanceId;
        this.executionId = executionId;
    }

    public UUID getId() {
        return id;
    }

    public OffsetDateTime getStarted() {
        return started;
    }

    public OffsetDateTime getEnded() {
        return ended;
    }

    public OffsetDateTime getLastUpdated() {
        return lastUpdated;
    }

    public BatchJobType getType() {
        return type;
    }

    public BatchJobStatus getStatus() {
        return status;
    }

    public Long getItemsProcessedSuccess() {
        return itemsProcessedByStatus.getOrDefault(BatchJobItemStatus.PROCESSED, 0L);
    }

    public Long getItemsProcessingFailed() {
        return itemsProcessedByStatus.getOrDefault(BatchJobItemStatus.FAILED, 0L);
    }

    public Long getItemsProcessingNotNecessary() {
        return itemsProcessedByStatus.getOrDefault(BatchJobItemStatus.NO_PROCESSING_NECESSARY, 0L);
    }

    public Long getTotalItemsProcessed() {
        return itemsProcessedByStatus.values().stream().mapToLong(Long::valueOf).sum();
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public Long getExecutionId() {
        return executionId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BatchJob batchJob)) return false;
        return Objects.equals(id, batchJob.id)
                && Objects.equals(started, batchJob.started)
                && Objects.equals(ended, batchJob.ended)
                && type == batchJob.type
                && status == batchJob.status
                && Objects.equals(itemsProcessedByStatus, batchJob.itemsProcessedByStatus)
                && Objects.equals(instanceId, batchJob.instanceId)
                && Objects.equals(lastUpdated, batchJob.lastUpdated)
                && Objects.equals(executionId, batchJob.executionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id, started, ended, type, status, itemsProcessedByStatus, instanceId, executionId, lastUpdated);
    }
}
