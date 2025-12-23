package it.mulders.traqqr.batch.locationlookup;

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
public class LocationLookupJobStarter {
    private static final Logger logger = LoggerFactory.getLogger(LocationLookupJobStarter.class);

    @Inject
    private JobOperator jobOperator;

    @Inject
    private SecurityWrapper wrapper;

    public LocationLookupJobStarter() {}

    protected LocationLookupJobStarter(JobOperator jobOperator, SecurityWrapper wrapper) {
        this.jobOperator = jobOperator;
        this.wrapper = wrapper;
    }

    public void startManual(@Observes @Any JobStartRequestedEvent event) {
        logger.debug("Received JobStartRequestedEvent: process_type={}", event.jobType());
        if (event.jobType() == BatchJobType.LOCATION_LOOKUP) {
            doStart();
        }
    }

    //    @Scheduled(dayOfMonth = "*", hour = "*", minute = "45")
    public void startAutomatic() {
        wrapper.execute(() -> {
            doStart();
            return null;
        });
    }

    void doStart() {
        var executionId = jobOperator.start("location-lookup", new Properties());
        var jobInstance = jobOperator.getJobInstance(executionId);
        var jobExecution = jobOperator.getJobExecution(executionId);
        logger.info(
                "Requested Location Lookup job start; execution_id={}, instance_id={}, status={}",
                executionId,
                jobInstance.getInstanceId(),
                jobExecution.getBatchStatus());
    }
}
