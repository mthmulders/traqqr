package it.mulders.traqqr.domain.measurements.api;

import it.mulders.traqqr.domain.measurements.Measurement;

public interface LookupLocationService {
    LookupLocationOutcome lookupLocation(Measurement measurement);
    LookupLocationOutcome refreshLocation(Measurement measurement);

    enum LookupLocationOutcome {
        SUCCESS,
        NOT_FOUND,
        NOT_NECESSARY,
        FAILURE
    }
}
