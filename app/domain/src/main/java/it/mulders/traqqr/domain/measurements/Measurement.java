package it.mulders.traqqr.domain.measurements;

import it.mulders.traqqr.domain.vehicles.Vehicle;
import java.time.OffsetDateTime;
import java.util.UUID;

public record Measurement(
    UUID id,
    Vehicle vehicle,
    OffsetDateTime timestamp,
    int odometer,
    Battery battery,
    Location location
) {
    public record Battery(int soc) {}
    public record Location(double lat, double lon) {}
}
