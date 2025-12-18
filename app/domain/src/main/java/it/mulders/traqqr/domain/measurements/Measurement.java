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
        Source source,
        Vehicle vehicle) {
    public Measurement withRegistrationTimestamp(OffsetDateTime registrationTimestamp) {
        return new Measurement(
                id, registrationTimestamp, measurementTimestamp, odometer, battery, location, source, vehicle);
    }

    public Measurement withVehicle(Vehicle vehicle) {
        return new Measurement(
                id, registrationTimestamp, measurementTimestamp, odometer, battery, location, source, vehicle);
    }

    public Measurement withSource(Source source) {
        return new Measurement(
                id, registrationTimestamp, measurementTimestamp, odometer, battery, location, source, vehicle);
    }

    public Measurement withLocation(Location location) {
        return new Measurement(
                id, registrationTimestamp, measurementTimestamp, odometer, battery, location, source, vehicle);
    }

    public record Battery(byte soc) {}

    public record Location(double lat, double lon, String description) {
        public Location(double lat, double lon) {
            this(lat, lon, null);
        }
    }
}
