package it.mulders.traqqr.api.measurements;

import java.time.OffsetDateTime;

public record MeasurementDto(
    OffsetDateTime timestamp,
    int odometer,
    Battery battery,
    Location location
) {
    public record Battery(int soc) {}
    public record Location(double lat, double lon) {}
}
