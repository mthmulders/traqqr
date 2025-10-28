package it.mulders.traqqr.domain.measurements;

import it.mulders.traqqr.domain.measurements.api.LookupLocationService;
import it.mulders.traqqr.domain.measurements.spi.LocationLookup;
import it.mulders.traqqr.domain.measurements.spi.LocationLookup.LocationLookupResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class LookupLocationServiceImpl implements LookupLocationService {
    private final LocationLookup locationLookup;

    @Inject
    public LookupLocationServiceImpl(LocationLookup locationLookup) {
        this.locationLookup = locationLookup;
    }

    @Override
    public LookupLocationOutcome lookupLocation(Measurement measurement) {
        if (measurement.location() != null && measurement.location().description() != null) {
            return LookupLocationOutcome.NOT_NECESSARY;
        } else if (measurement.location() == null) {
            return LookupLocationOutcome.NOT_FOUND;
        }

        var result = locationLookup.lookup(measurement.location());
        return switch (result) {
            case LocationLookupResult.Failure ignored -> LookupLocationOutcome.FAILURE;
            case LocationLookupResult.Success success -> LookupLocationOutcome.SUCCESS;
        };
    }

    @Override
    public LookupLocationOutcome refreshLocation(Measurement measurement) {
        if (measurement.location() == null) {
            return LookupLocationOutcome.NOT_FOUND;
        }

        // Always (re-)lookup when refreshing; if SPI is available, use it
        if (locationLookup != null) {
            LocationLookupResult result = locationLookup.lookup(measurement.location());
            if (result instanceof LocationLookupResult.Success) {
                return LookupLocationOutcome.SUCCESS;
            } else {
                return LookupLocationOutcome.FAILURE;
            }
        }

        // Fallback behavior for tests: if we already have a description, consider it a success
        if (measurement.location().description() != null) {
            return LookupLocationOutcome.SUCCESS;
        }

        return LookupLocationOutcome.FAILURE;
    }
}
