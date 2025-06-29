package it.mulders.traqqr.batch.example;

import it.mulders.traqqr.domain.batch.BatchJobType;
import it.mulders.traqqr.domain.batch.JobStartRequestedEvent;
import jakarta.annotation.security.RunAs;
import jakarta.batch.operations.JobOperator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@RunAs("batchSubmitter")
public class ExampleJob {
    private static final Logger logger = LoggerFactory.getLogger(ExampleJob.class);

    @Inject
    private JobOperator jobOperator;

    public void startManual(@Observes @Any JobStartRequestedEvent event) {
        logger.debug("Received JobStartRequestedEvent: process_type={}", event.jobType());
        if (event.jobType() == BatchJobType.EXAMPLE) {
            doStart();
        }
    }

    private void doStart() {
        var executionId = jobOperator.start("example", new Properties());
        var jobInstance = jobOperator.getJobInstance(executionId);
        var jobExecution = jobOperator.getJobExecution(executionId);
        logger.info(
                "Requested Example job start; execution_id={}, instance_id={}, status={}",
                executionId,
                jobInstance.getInstanceId(),
                jobExecution.getBatchStatus());
    }
}
