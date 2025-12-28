package it.mulders.traqqr.web.batch;

import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.BatchJobType;
import it.mulders.traqqr.domain.batch.JobStartRequestedEvent;
import it.mulders.traqqr.domain.batch.spi.BatchJobRepository;
import jakarta.enterprise.event.Event;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("manageBatchJobsView")
@ViewScoped
public class ManageBatchJobsView implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(ManageBatchJobsView.class);

    // Components
    private final Event<JobStartRequestedEvent> jobStartRequestedEvent;

    // Data
    private final Collection<BatchJobType> batchJobTypes = List.of(BatchJobType.values());
    private final LazyDataModel<BatchJob> filteredBatchJobs;
    private final LazyDataModel<BatchJob> latestRuns;

    @Inject
    public ManageBatchJobsView(
            final BatchJobRepository batchJobRepository, final Event<JobStartRequestedEvent> jobStartRequestedEvent) {
        this.jobStartRequestedEvent = jobStartRequestedEvent;
        this.filteredBatchJobs = new LazyBatchJobDataModel(batchJobRepository);
        this.latestRuns = new LazyLatestBatchJobRunsDataModel(batchJobRepository);
    }

    public void startExampleBatchJob() {
        startBatchJob(BatchJobType.EXAMPLE);
    }

    public void startLocationLookupBatchJob() {
        startBatchJob(BatchJobType.LOCATION_LOOKUP);
    }

    private void startBatchJob(BatchJobType type) {
        log.info("Starting batch job; type={}", type);
        var event = new JobStartRequestedEvent(type);
        jobStartRequestedEvent.fire(event);
    }

    public Collection<BatchJobType> getBatchJobTypes() {
        return batchJobTypes;
    }

    public LazyDataModel<BatchJob> getFilteredBatchJobs() {
        return filteredBatchJobs;
    }

    public LazyDataModel<BatchJob> getLatestRuns() {
        return latestRuns;
    }
}
