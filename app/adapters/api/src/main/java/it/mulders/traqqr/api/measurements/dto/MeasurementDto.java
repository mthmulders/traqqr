package it.mulders.traqqr.api.measurements.dto;

import java.time.OffsetDateTime;

public record MeasurementDto(OffsetDateTime timestamp, int odometer, BatteryDto battery, LocationDto location) {
    public record BatteryDto(byte soc) {}

    public record LocationDto(double lat, double lon) {}
}
