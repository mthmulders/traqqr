package it.mulders.traqqr.batch.example;

import it.mulders.traqqr.batch.shared.TraqqrProcessor;
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
public class ExampleProcessor extends TraqqrProcessor implements ItemProcessor {
    private final Logger logger = LoggerFactory.getLogger(ExampleProcessor.class);

    @Inject
    public ExampleProcessor(final JobContext jobContext) {
        super(jobContext);
    }

    @Override
    public Object processItem(Object item) {
        var measurement = (Measurement) item;
        logger.info("Processing item; measurement_id={}", measurement.id());
        return new BatchJobItem<>(getBatchJob(), BatchJobItemStatus.PROCESSED, measurement);
    }
}
