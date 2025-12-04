package it.mulders.traqqr.batch.shared;

import static it.mulders.traqqr.batch.shared.Constants.BATCH_JOB_PROPERTY;

import it.mulders.traqqr.domain.batch.BatchJob;
import jakarta.batch.runtime.context.JobContext;

public abstract class TraqqrProcessor {
    // Data
    private final JobContext jobContext;

    public TraqqrProcessor(final JobContext jobContext) {
        this.jobContext = jobContext;
    }

    protected BatchJob getBatchJob() {
        return (BatchJob) jobContext.getProperties().get(BATCH_JOB_PROPERTY);
    }
}
