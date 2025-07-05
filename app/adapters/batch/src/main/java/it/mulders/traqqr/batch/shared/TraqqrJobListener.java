package it.mulders.traqqr.batch.shared;

import static it.mulders.traqqr.batch.shared.Constants.BATCH_JOB_PROPERTY;

import jakarta.batch.api.listener.AbstractJobListener;
import jakarta.batch.operations.JobOperator;
import jakarta.batch.runtime.context.JobContext;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
@Named("traqqrJobListener")
public class TraqqrJobListener extends AbstractJobListener {
    private final Logger logger = LoggerFactory.getLogger(TraqqrJobListener.class);

    // Components
    private final BatchJobConverter batchJobConverter;
    private final JobOperator jobOperator;

    // Data
    private final JobContext jobContext;

    @Inject
    public TraqqrJobListener(
            final JobOperator jobOperator, final JobContext jobContext, final BatchJobConverter batchJobConverter) {
        this.batchJobConverter = batchJobConverter;
        this.jobContext = jobContext;
        this.jobOperator = jobOperator;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void beforeJob() {
        var instance = jobOperator.getJobInstance(jobContext.getInstanceId());
        var execution = jobOperator.getJobExecution(jobContext.getExecutionId());

        logger.info(
                "Starting job; type={}, instance_id={}, execution_id={}",
                jobContext.getJobName(),
                instance.getInstanceId(),
                execution.getExecutionId());

        var batchJob = batchJobConverter.convert(instance, execution);
        this.jobContext.getProperties().put(BATCH_JOB_PROPERTY, batchJob);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void afterJob() {
        logger.info(
                "Job completed; type={}, instance_id={}, execution_id={}",
                jobContext.getJobName(),
                jobContext.getInstanceId(),
                jobContext.getExecutionId());
    }
}
