package it.mulders.traqqr.domain.measurements;

import it.mulders.traqqr.domain.vehicles.Vehicle;
import java.time.OffsetDateTime;
import java.util.UUID;

public record Measurement(
        UUID id,
        OffsetDateTime registrationTimestamp,
        OffsetDateTime measurementTimestamp,
        int odometer,
        Battery battery,
        Location location,
        Vehicle vehicle) {

    public record Battery(byte soc) {}

    public record Location(double lat, double lon) {}
}
