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
    private final BatchJobRepository batchJobRepository;

    // Data
    private final Collection<BatchJobType> batchJobTypes = List.of(BatchJobType.values());
    private LazyDataModel<BatchJob> batchJobsForSelectedBatchJobType;
    private BatchJobType selectedBatchJobType;

    @Inject
    public ManageBatchJobsView(
            final BatchJobRepository batchJobRepository, final Event<JobStartRequestedEvent> jobStartRequestedEvent) {
        this.batchJobRepository = batchJobRepository;
        this.jobStartRequestedEvent = jobStartRequestedEvent;
        this.batchJobsForSelectedBatchJobType = new LazyBatchJobDataModel(batchJobRepository, null);
    }

    public void startBatchJob() {
        log.info("Starting batch job; type={}", selectedBatchJobType);
        var event = new JobStartRequestedEvent(selectedBatchJobType);
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

    public void populateBatchJobsForSelectedBatchJobType() {
        if (batchJobsForSelectedBatchJobType != null) {
            log.info("Finding batch jobs; batch_job_type={}", this.selectedBatchJobType.name());
        } else {
            log.info("Finding batch jobs");
        }
        this.batchJobsForSelectedBatchJobType = new LazyBatchJobDataModel(batchJobRepository, selectedBatchJobType);
    }

    public LazyDataModel<BatchJob> getBatchJobsForSelectedBatchJobType() {
        return batchJobsForSelectedBatchJobType;
    }
}
