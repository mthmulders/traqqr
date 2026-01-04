package it.mulders.traqqr.web.batch;

import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.spi.BatchJobRepository;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("showBatchJobView")
@ViewScoped
public class ShowBatchJobView implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(ShowBatchJobView.class);

    // Components
    private final BatchJobRepository batchJobRepository;

    // Data
    private UUID id;
    private BatchJob batchJob;

    @Inject
    public ShowBatchJobView(final BatchJobRepository batchJobRepository) {
        this.batchJobRepository = batchJobRepository;
    }

    public void loadBatchJob() {
        this.batchJobRepository.findById(id).ifPresent(item -> this.batchJob = item);
    }

    public String showBatchJob(final BatchJob batchJob) {
        this.id = batchJob.getId();
        return "/secure/batch/show/index.xhtml?faces-redirect=true&includeViewParams=true";
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setBatchJob(BatchJob batchJob) {
        this.batchJob = batchJob;
    }

    public BatchJob getBatchJob() {
        return batchJob;
    }
}
