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
@Named("locationLookupProcessor")
public class LocationLookupProcessor extends TraqqrProcessor implements ItemProcessor {
    private static final String LOCATION_NOT_FOUND_MESSAGE = "No location found for coordinates %s,%s.";
    private static final String FAILURE_MESSAGE = "Failure looking up description for coordinates %s,%s: %s.";

    private final Logger logger = LoggerFactory.getLogger(LocationLookupProcessor.class);

    // Components
    private final LookupLocationService lookupLocationService;

    @Inject
    public LocationLookupProcessor(final JobContext jobContext, final LookupLocationService lookupLocationService) {
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
                var message = LOCATION_NOT_FOUND_MESSAGE.formatted(
                        measurement.location().lat(), measurement.location().lon());
                yield new BatchJobItem<>(getBatchJob(), BatchJobItemStatus.FAILED, measurement, message);
            }
            case Failure(var cause) -> {
                logger.error("Location lookup failed; measurement_id={}", measurement.id(), cause);
                var message = FAILURE_MESSAGE.formatted(
                        measurement.location().lat(), measurement.location().lon(), cause.getMessage());
                yield new BatchJobItem<>(getBatchJob(), BatchJobItemStatus.FAILED, measurement, message);
            }
        };
    }
}
