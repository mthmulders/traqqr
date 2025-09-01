package it.mulders.traqqr.web.batch;

import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.BatchJobRepository;
import it.mulders.traqqr.domain.batch.BatchJobType;
import it.mulders.traqqr.domain.batch.JobStartRequestedEvent;
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
    private LazyDataModel<BatchJob> batchJobs;
    private LazyDataModel<BatchJob> latestRuns;
    private BatchJobType selectedBatchJobType;

    @Inject
    public ManageBatchJobsView(
            final BatchJobRepository batchJobRepository, final Event<JobStartRequestedEvent> jobStartRequestedEvent) {
        this.jobStartRequestedEvent = jobStartRequestedEvent;
        this.batchJobs = new LazyBatchJobDataModel(batchJobRepository);
        this.latestRuns = new LazyLatestBatchJobRunsDataModel(batchJobRepository);
    }

    public void startExampleBatchJob() {
        startBatchJob(BatchJobType.EXAMPLE);
    }

    private void startBatchJob(BatchJobType type) {
        log.info("Starting batch job; type={}", type);
        var event = new JobStartRequestedEvent(type);
        jobStartRequestedEvent.fire(event);
    }

    public Collection<BatchJobType> getBatchJobTypes() {
        return batchJobTypes;
    }

    public BatchJobType getSelectedBatchJobType() {
        return selectedBatchJobType;
    }

    public void setSelectedBatchJobType(BatchJobType selectedBatchJobType) {
        this.selectedBatchJobType = selectedBatchJobType;
    }

    public LazyDataModel<BatchJob> getBatchJobs() {
        return batchJobs;
    }

    public LazyDataModel<BatchJob> getLatestRuns() {
        return latestRuns;
    }
}
