package it.mulders.traqqr.domain.measurements;

import it.mulders.traqqr.domain.measurements.api.LookupLocationService;
import it.mulders.traqqr.domain.measurements.spi.LocationLookup;
import it.mulders.traqqr.domain.measurements.spi.LocationLookup.LocationLookupResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class LookupLocationServiceImpl implements LookupLocationService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LocationLookup locationLookup;

    @Inject
    public LookupLocationServiceImpl(LocationLookup locationLookup) {
        this.locationLookup = locationLookup;
    }

    @Override
    public LookupLocationOutcome lookupLocation(Measurement measurement) {
        if (measurement.location() != null
                && measurement.location().description() != null
                && !measurement.location().description().isEmpty()) {
            logger.info("Measurement already has location description; measurement_id={}", measurement.id());
            return LookupLocationOutcome.NOT_NECESSARY;
        }

        return refreshLocation(measurement);
    }

    @Override
    public LookupLocationOutcome refreshLocation(Measurement measurement) {
        if (measurement.location() == null) {
            logger.warn(
                    "Can't perform location lookup for measurement without location; measurement_id={}",
                    measurement.id());
            return LookupLocationOutcome.NOT_FOUND;
        }

        var result = locationLookup.lookup(measurement.location());
        return switch (result) {
            case LocationLookupResult.Failure(var cause) -> new LookupLocationOutcome.Failure(cause);
            case LocationLookupResult.NotFound ignored -> LookupLocationOutcome.NOT_FOUND;
            case LocationLookupResult.Success(var location) -> new LookupLocationOutcome.Success(location);
        };
    }
}
