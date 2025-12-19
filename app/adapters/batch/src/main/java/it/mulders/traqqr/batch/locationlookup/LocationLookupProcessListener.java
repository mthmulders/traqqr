package it.mulders.traqqr.batch.locationlookup;

import it.mulders.traqqr.batch.shared.TraqqrProcessor;
import it.mulders.traqqr.domain.batch.BatchJobItem;
import it.mulders.traqqr.domain.batch.BatchJobItemStatus;
import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.api.LookupLocationService;
import it.mulders.traqqr.domain.measurements.api.LookupLocationService.LookupLocationOutcome;
import it.mulders.traqqr.domain.measurements.api.LookupLocationService.LookupLocationOutcome.Failure;
import it.mulders.traqqr.domain.measurements.api.LookupLocationService.LookupLocationOutcome.NotFound;
import it.mulders.traqqr.domain.measurements.api.LookupLocationService.LookupLocationOutcome.Success;
import jakarta.batch.api.chunk.ItemProcessor;
import jakarta.batch.runtime.context.JobContext;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
@Named("locationLookupProcessListener")
public class LocationLookupProcessListener extends TraqqrProcessor implements ItemProcessor {
    private final Logger logger = LoggerFactory.getLogger(LocationLookupProcessListener.class);

    // Components
    private final LookupLocationService lookupLocationService;

    @Inject
    public LocationLookupProcessListener(
            final JobContext jobContext, final LookupLocationService lookupLocationService) {
        super(jobContext);
        this.lookupLocationService = lookupLocationService;
    }

    @Override
    public Object processItem(Object item) throws Exception {
        var measurement = (Measurement) item;

        var result = lookupLocationService.lookupLocation(measurement);
        return switch (result) {
            case LookupLocationOutcome.NotNecessary ignored -> {
                logger.info("Location lookup not necessary; measurement_id={}", measurement.id());
                yield new BatchJobItem<>(getBatchJob(), BatchJobItemStatus.NO_PROCESSING_NECESSARY, measurement);
            }
            case Success(var location) -> {
                logger.info("Location lookup successful; measurement_id={}", measurement.id());
                var updatedMeasurement = measurement.withLocation(location);
                yield new BatchJobItem<>(getBatchJob(), BatchJobItemStatus.PROCESSED, updatedMeasurement);
            }
            case NotFound ignored -> {
                logger.info("Location lookup: location not found; measurement_id={}", measurement.id());
                yield new BatchJobItem<>(getBatchJob(), BatchJobItemStatus.FAILED, measurement);
            }
            case Failure(var cause) -> {
                logger.error("Location lookup failed; measurement_id={}", measurement.id(), cause);
                yield new BatchJobItem<>(getBatchJob(), BatchJobItemStatus.FAILED, cause);
            }
        };
    }
}
