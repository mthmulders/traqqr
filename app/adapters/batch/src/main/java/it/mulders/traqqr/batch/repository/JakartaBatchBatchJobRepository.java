package it.mulders.traqqr.batch.repository;

import static java.util.Comparator.comparing;

import it.mulders.traqqr.batch.shared.BatchJobConverter;
import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.BatchJobItemStatus;
import it.mulders.traqqr.domain.batch.BatchJobType;
import it.mulders.traqqr.domain.batch.spi.BatchJobItemRepository;
import it.mulders.traqqr.domain.batch.spi.BatchJobRepository;
import it.mulders.traqqr.domain.shared.Pagination;
import jakarta.batch.operations.JobOperator;
import jakarta.batch.runtime.JobExecution;
import jakarta.batch.runtime.JobInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class JakartaBatchBatchJobRepository implements BatchJobRepository {
    private static final Logger log = LoggerFactory.getLogger(JakartaBatchBatchJobRepository.class);

    private final BatchJobConverter batchJobConverter;
    private final BatchJobItemRepository itemRepository;
    private final JobOperator jobOperator;

    @Inject
    public JakartaBatchBatchJobRepository(
            final JobOperator jobOperator,
            final BatchJobItemRepository itemRepository,
            final BatchJobConverter batchJobConverter) {
        this.batchJobConverter = batchJobConverter;
        this.itemRepository = itemRepository;
        this.jobOperator = jobOperator;
    }

    @Override
    public Collection<BatchJob> findPaginated(final BatchJobType batchJobType, final Pagination pagination) {
        var jobName = batchJobConverter.jobNameFromBatchJobType(batchJobType);
        log.debug("Find all jobs; job_name={}, offset={}, limit={}", jobName, pagination.offset(), pagination.limit());

        return fetchJobExecutions(jobName, pagination.offset(), pagination.limit())
                .flatMap(this::fetchJobExecutions)
                .map(this::fetchItemsForExecution)
                .map(this::mapToDomain)
                .toList();
    }

    private JobInstanceWithExecutionAndItems fetchItemsForExecution(
            final JobInstanceWithExecution jobInstanceWithExecution) {
        var instance = jobInstanceWithExecution.instance;
        var execution = jobInstanceWithExecution.execution;
        log.debug(
                "Fetching job item counts; job_name={}, instance={}, execution={}",
                instance.getJobName(),
                instance.getInstanceId(),
                execution.getExecutionId());
        var itemsProcessed = itemRepository.findItemCountsForJobInstanceAndExecution(
                instance.getInstanceId(), execution.getExecutionId());

        return new JobInstanceWithExecutionAndItems(instance, execution, itemsProcessed);
    }

    private Stream<JobInstance> fetchLatestJobExecutions(String jobName) {
        return fetchJobExecutions(jobName, 0, 1);
    }

    private Stream<JobInstance> fetchJobExecutions(String jobName, int offset, int limit) {
        log.debug("Fetching job executions; job_name={}, offset={}, limit={}", jobName, offset, limit);
        return jobOperator.getJobInstances(jobName, offset, limit).stream();
    }

    private Stream<JobInstanceWithExecution> fetchJobExecutions(JobInstance jobInstance) {
        log.debug(
                "Fetching job executions; job_name={}, instance={}",
                jobInstance.getJobName(),
                jobInstance.getInstanceId());
        var executions = jobOperator.getJobExecutions(jobInstance);
        if (executions.size() > 1) {
            log.warn(
                    "Found multiple job executions; job_instance={}, job_name={}",
                    jobInstance.getInstanceId(),
                    jobInstance.getJobName());
        }
        return executions.stream().map(execution -> new JobInstanceWithExecution(jobInstance, execution));
    }

    private BatchJob mapToDomain(final JobInstanceWithExecutionAndItems jobInstanceWithExecutionAndItems) {
        var instance = jobInstanceWithExecutionAndItems.instance;
        var execution = jobInstanceWithExecutionAndItems.execution;
        var itemsProcessed = jobInstanceWithExecutionAndItems.itemsProcessed;

        return batchJobConverter.convert(instance, execution, itemsProcessed);
    }

    @Override
    public long count() {
        return jobOperator.getJobNames().stream()
                .mapToInt(jobOperator::getJobInstanceCount)
                .sum();
    }

    @Override
    public long count(BatchJobType batchJobType) {
        var jobName = batchJobConverter.jobNameFromBatchJobType(batchJobType);
        return jobOperator.getJobInstanceCount(jobName);
    }

    @Override
    public List<BatchJob> findLatestRunsPerBatchJobType() {
        return Arrays.stream(BatchJobType.values())
                .map(batchJobConverter::jobNameFromBatchJobType)
                .flatMap(this::fetchLatestJobExecutions)
                .flatMap(this::fetchJobExecutions)
                .map(this::fetchItemsForExecution)
                .map(this::mapToDomain)
                .sorted(comparing(BatchJob::getLastUpdated))
                .toList();
    }

    @Override
    public Optional<BatchJob> findById(UUID id) {
        itemRepository.findById(id).map(item -> {
            var instance = jobOperator.getJobInstance(item.batchJob().getInstanceId());
            var execution = jobOperator.getJobExecution(item.batchJob().getExecutionId());

            return batchJobConverter.convert(instance, execution);
        });

        return Optional.empty();
    }

    private record JobInstanceWithExecution(JobInstance instance, JobExecution execution) {}

    private record JobInstanceWithExecutionAndItems(
            JobInstance instance, JobExecution execution, Map<BatchJobItemStatus, Long> itemsProcessed) {}
}
