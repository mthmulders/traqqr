package it.mulders.traqqr.batch.example;

import static it.mulders.traqqr.batch.shared.Constants.BATCH_JOB_PROPERTY;

import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.BatchJobItem;
import it.mulders.traqqr.domain.batch.BatchJobItemStatus;
import it.mulders.traqqr.domain.measurements.Measurement;
import jakarta.batch.api.chunk.ItemProcessor;
import jakarta.batch.runtime.context.JobContext;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
@Named("exampleProcessor")
public class ExampleProcessor implements ItemProcessor {
    private final Logger logger = LoggerFactory.getLogger(ExampleProcessor.class);

    // Data
    private final JobContext jobContext;

    @Inject
    public ExampleProcessor(final JobContext jobContext) {
        this.jobContext = jobContext;
    }

    @Override
    public Object processItem(Object item) {
        var measurement = (Measurement) item;
        logger.info("Processing item; measurement_id={}", measurement.id());
        return new BatchJobItem<>(getBatchJob(), BatchJobItemStatus.PROCESSED, measurement);
    }

    private BatchJob getBatchJob() {
        return (BatchJob) jobContext.getProperties().get(BATCH_JOB_PROPERTY);
    }
}
