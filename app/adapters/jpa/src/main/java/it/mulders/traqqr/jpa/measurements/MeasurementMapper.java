package it.mulders.traqqr.jpa.measurements;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.jpa.batch.BatchJobItemMapper;
import it.mulders.traqqr.jpa.vehicles.VehicleMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(
        componentModel = ComponentModel.JAKARTA_CDI,
        nullValueIterableMappingStrategy = RETURN_DEFAULT,
        uses = {BatchJobItemMapper.class, VehicleMapper.class},
        injectionStrategy = CONSTRUCTOR)
public interface MeasurementMapper {
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(source = "registrationTimestamp", target = "registeredAt")
    @Mapping(source = "measurementTimestamp", target = "measuredAt")
    @Mapping(source = "location", target = "gpsLocation")
    @Mapping(source = "location.description", target = "locationDescription")
    MeasurementEntity measurementToMeasurementEntity(final Measurement measurement);

    BatteryEntity batteryToBatteryEntity(final Measurement.Battery battery);

    @Mapping(source = "lat", target = "latitude")
    @Mapping(source = "lon", target = "longitude")
    GpsLocationEntity locationToGpsLocationEntity(final Measurement.Location location);

    @Mapping(expression = "java(gpsLocationEntityToLocation(measurement))", target = "location")
    @Mapping(source = "registeredAt", target = "registrationTimestamp")
    @Mapping(source = "measuredAt", target = "measurementTimestamp")
    Measurement measurementEntityToMeasurement(MeasurementEntity measurement);

    Measurement.Battery batteryEntityToBattery(BatteryEntity batteryEntity);

    @Mapping(source = "gpsLocation.latitude", target = "lat")
    @Mapping(source = "gpsLocation.longitude", target = "lon")
    @Mapping(source = "locationDescription", target = "description")
    Measurement.Location gpsLocationEntityToLocation(MeasurementEntity measurement);
}
