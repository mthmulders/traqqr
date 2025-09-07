package it.mulders.traqqr.domain.measurements.api;

import it.mulders.traqqr.domain.measurements.Measurement;

public interface RegisterMeasurementService {
    RegisterMeasurementOutcome registerAutomatedMeasurement(String vehicleCode, String apiKey, Measurement measurement);

    enum RegisterMeasurementOutcome {
        SUCCESS,
        UNAUTHORIZED,
        UNKNOWN_VEHICLE
    }
}
