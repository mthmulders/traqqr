package it.mulders.traqqr.domain.measurements.api;

import it.mulders.traqqr.domain.measurements.Measurement;

public interface LookupLocationService {
    LookupLocationOutcome lookupLocation(Measurement measurement);

    LookupLocationOutcome refreshLocation(Measurement measurement);

    sealed interface LookupLocationOutcome {
        record Success(Measurement.Location location) implements LookupLocationOutcome {}

        record NotFound() implements LookupLocationOutcome {}

        record NotNecessary() implements LookupLocationOutcome {}

        record Failure(Throwable throwable) implements LookupLocationOutcome {}

        LookupLocationOutcome NOT_FOUND = new NotFound();
        LookupLocationOutcome NOT_NECESSARY = new NotNecessary();
    }
}
