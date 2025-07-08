package it.mulders.traqqr.domain.fakes;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.Source;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import java.time.OffsetDateTime;
import java.util.UUID;

public class MeasurementFaker {
    public static Measurement createMeasurement(Vehicle vehicle) {
        var now = OffsetDateTime.now();
        return new Measurement(
                UUID.randomUUID(),
                now,
                now.minusSeconds(5),
                1_000,
                new Measurement.Battery((byte) 80),
                new Measurement.Location(55.0, 6.0),
                Source.API,
                vehicle);
    }
}
