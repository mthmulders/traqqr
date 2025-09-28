package it.mulders.traqqr.batch.example;

import it.mulders.traqqr.batch.scheduling.Scheduled;
import it.mulders.traqqr.domain.batch.BatchJobType;
import it.mulders.traqqr.domain.batch.JobStartRequestedEvent;
import it.mulders.traqqr.domain.shared.spi.SecurityWrapper;
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
public class ExampleJobStarter {
    private static final Logger logger = LoggerFactory.getLogger(ExampleJobStarter.class);

    @Inject
    private JobOperator jobOperator;

    @Inject
    private SecurityWrapper wrapper;

    public ExampleJobStarter() {}

    protected ExampleJobStarter(JobOperator jobOperator, SecurityWrapper wrapper) {
        this.jobOperator = jobOperator;
        this.wrapper = wrapper;
    }

    public void startManual(@Observes @Any JobStartRequestedEvent event) {
        logger.debug("Received JobStartRequestedEvent: process_type={}", event.jobType());
        if (event.jobType() == BatchJobType.EXAMPLE) {
            doStart();
        }
    }

    @Scheduled(dayOfMonth = "*", hour = "*", minute = "15")
    public void startAutomatic() {
        wrapper.execute(() -> {
            doStart();
            return null;
        });
    }

    void doStart() {
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
