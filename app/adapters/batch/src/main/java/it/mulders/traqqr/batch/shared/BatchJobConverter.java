package it.mulders.traqqr.batch.shared;

import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.BatchJobItemStatus;
import it.mulders.traqqr.domain.batch.BatchJobStatus;
import it.mulders.traqqr.domain.batch.BatchJobType;
import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.JobExecution;
import jakarta.batch.runtime.JobInstance;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class BatchJobConverter {
    public BatchJob convert(final JobInstance instance, final JobExecution execution) {
        return convert(instance, execution, new HashMap<>());
    }

    public BatchJob convert(
            JobInstance instance, JobExecution execution, Map<BatchJobItemStatus, Long> itemsProcessed) {
        // In the domain, the technical difference between a JobInstance and a JobExecution
        // is not relevant. All Jobs must be declared as `restartable="false"`.
        return new BatchJob(
                fromDate(execution.getStartTime()),
                fromDate(execution.getEndTime()),
                fromDate(execution.getLastUpdatedTime()),
                fromJobName(instance.getJobName()),
                fromBatchStatus(execution.getBatchStatus()),
                itemsProcessed,
                instance.getInstanceId(),
                execution.getExecutionId());
    }

    private OffsetDateTime fromDate(Date input) {
        return input == null ? null : OffsetDateTime.ofInstant(input.toInstant(), ZoneId.systemDefault());
    }

    private BatchJobType fromJobName(final String jobName) {
        return switch (jobName) {
            case "example" -> BatchJobType.EXAMPLE;
            default -> throw new IllegalStateException("Unexpected job name: " + jobName);
        };
    }

    private BatchJobStatus fromBatchStatus(final BatchStatus batchStatus) {
        return switch (batchStatus) {
            case COMPLETED -> BatchJobStatus.COMPLETED;
            case FAILED -> BatchJobStatus.FAILED;
            case STARTING, STARTED -> BatchJobStatus.RUNNING;
            default -> throw new IllegalStateException("Unexpected batch status: " + batchStatus);
        };
    }
}
