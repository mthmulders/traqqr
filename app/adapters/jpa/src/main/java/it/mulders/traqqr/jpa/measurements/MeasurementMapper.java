package it.mulders.traqqr.jpa.measurements;

import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.jpa.vehicles.VehicleMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(
        componentModel = ComponentModel.JAKARTA_CDI,
        nullValueIterableMappingStrategy = RETURN_DEFAULT,
        uses = {VehicleMapper.class})
public interface MeasurementMapper {
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(source = "registrationTimestamp", target = "registeredAt")
    @Mapping(source = "measurementTimestamp", target = "measuredAt")
    @Mapping(source = "location", target = "gpsLocation")
    MeasurementEntity measurementToMeasurementEntity(final Measurement measurement);

    BatteryEntity batteryToBatteryEntity(final Measurement.Battery battery);

    @Mapping(source = "lat", target = "latitude")
    @Mapping(source = "lon", target = "longitude")
    GpsLocationEntity locationToGpsLocationEntity(final Measurement.Location location);

    @Mapping(source = "gpsLocation", target = "location")
    @Mapping(source = "registeredAt", target = "registrationTimestamp")
    @Mapping(source = "measuredAt", target = "measurementTimestamp")
    Measurement measurementEntityToMeasurement(MeasurementEntity measurement);

    Measurement.Battery batteryEntityToBattery(BatteryEntity batteryEntity);

    @Mapping(source = "latitude", target = "lat")
    @Mapping(source = "longitude", target = "lon")
    Measurement.Location gpsLocationEntityToLocation(GpsLocationEntity gpsLocationEntity);
}
