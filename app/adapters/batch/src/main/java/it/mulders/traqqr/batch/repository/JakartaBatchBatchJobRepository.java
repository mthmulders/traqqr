package it.mulders.traqqr.batch.repository;

import it.mulders.traqqr.batch.shared.BatchJobConverter;
import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.BatchJobItemRepository;
import it.mulders.traqqr.domain.batch.BatchJobItemStatus;
import it.mulders.traqqr.domain.batch.BatchJobRepository;
import it.mulders.traqqr.domain.shared.Pagination;
import jakarta.batch.operations.JobOperator;
import jakarta.batch.runtime.JobExecution;
import jakarta.batch.runtime.JobInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Collection;
import java.util.Map;
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
    public Collection<BatchJob> findPaginated(Pagination pagination) {
        return jobOperator.getJobNames().stream()
                .parallel()
                .flatMap(jobName -> fetchJobExecutions(jobName, pagination))
                .flatMap(this::fetchJobExecutions)
                .map(this::fetchItemsForExecution)
                .map(this::mapToDomain)
                .toList();
    }

    private JobInstanceWithExecutionAndItems fetchItemsForExecution(
            final JobInstanceWithExecution jobInstanceWithExecution) {
        var instance = jobInstanceWithExecution.instance;
        var execution = jobInstanceWithExecution.execution;
        var itemsProcessed = itemRepository.findItemCountsForJobInstanceAndExecution(
                instance.getInstanceId(), execution.getExecutionId());

        return new JobInstanceWithExecutionAndItems(instance, execution, itemsProcessed);
    }

    private Stream<JobInstance> fetchJobExecutions(String jobName, Pagination pagination) {
        return jobOperator.getJobInstances(jobName, pagination.offset(), pagination.limit()).stream();
    }

    private Stream<JobInstanceWithExecution> fetchJobExecutions(JobInstance jobInstance) {
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
                .parallel()
                .mapToInt(jobOperator::getJobInstanceCount)
                .sum();
    }

    private record JobInstanceWithExecution(JobInstance instance, JobExecution execution) {}

    private record JobInstanceWithExecutionAndItems(
            JobInstance instance, JobExecution execution, Map<BatchJobItemStatus, Long> itemsProcessed) {}
}
