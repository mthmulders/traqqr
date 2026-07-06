package it.mulders.traqqr.web.batch;

import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.spi.BatchJobRepository;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("showBatchJobView")
@ViewScoped
public class ShowBatchJobView implements Serializable {
    // Components
    private final FacesContext facesContext;

    // Data
    private BatchJob batchJob;

    @Inject
    public ShowBatchJobView(final BatchJobRepository batchJobRepository, final FacesContext facesContext) {
        this.facesContext = facesContext;
    }

    public void loadBatchJob() {
        this.batchJob = (BatchJob) facesContext.getExternalContext().getFlash().get("selectedBatchJob");
    }

    public String showBatchJob(final BatchJob batchJob) {
        facesContext.getExternalContext().getFlash().put("selectedBatchJob", batchJob);
        return "/secure/batch/show/index.xhtml?faces-redirect=true&includeViewParams=true";
    }

    public void setBatchJob(BatchJob batchJob) {
        this.batchJob = batchJob;
    }

    public BatchJob getBatchJob() {
        return batchJob;
    }
}
