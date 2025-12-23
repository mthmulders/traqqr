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
import java.util.EnumMap;
import java.util.Map;

@ApplicationScoped
public class BatchJobConverter {
    // Data
    private final Map<BatchJobType, String> typeToNameMapping = new EnumMap<>(BatchJobType.class);

    public BatchJobConverter() {
        typeToNameMapping.put(BatchJobType.EXAMPLE, "example");
        typeToNameMapping.put(BatchJobType.LOCATION_LOOKUP, "location-lookup");
    }

    public BatchJob convert(final JobInstance instance, final JobExecution execution) {
        return convert(instance, execution, new EnumMap<>(BatchJobItemStatus.class));
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

    public BatchJobType fromJobName(final String jobName) {
        return typeToNameMapping.entrySet().stream()
                .filter(entry -> jobName.equals(entry.getValue()))
                .findAny()
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected job name: " + jobName));
    }

    public String jobNameFromBatchJobType(final BatchJobType batchJobType) {
        return typeToNameMapping.get(batchJobType);
    }

    BatchJobStatus fromBatchStatus(final BatchStatus batchStatus) {
        return switch (batchStatus) {
            case COMPLETED -> BatchJobStatus.COMPLETED;
            case FAILED -> BatchJobStatus.FAILED;
            case STARTING, STARTED -> BatchJobStatus.RUNNING;
            default -> throw new IllegalArgumentException("Unexpected batch status: " + batchStatus);
        };
    }
}
