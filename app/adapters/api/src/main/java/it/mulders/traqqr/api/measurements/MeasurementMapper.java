package it.mulders.traqqr.api.measurements;

import it.mulders.traqqr.api.measurements.dto.MeasurementDto;
import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import java.time.OffsetDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface MeasurementMapper {
    @Mapping(source = "registrationTimestamp", target = "registrationTimestamp")
    @Mapping(source = "measurementDto.timestamp", target = "measurementTimestamp")
    Measurement toMeasurement(Vehicle vehicle, MeasurementDto measurementDto, OffsetDateTime registrationTimestamp);
}
