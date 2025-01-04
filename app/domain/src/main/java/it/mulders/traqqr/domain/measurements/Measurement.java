package it.mulders.traqqr.domain.measurements;

import it.mulders.traqqr.domain.vehicles.Vehicle;
import java.time.OffsetDateTime;

public record Measurement(OffsetDateTime timestamp, int odometer, Battery battery, Location location, Vehicle vehicle) {

    public record Battery(byte soc) {}

    public record Location(double lat, double lon) {}
}
