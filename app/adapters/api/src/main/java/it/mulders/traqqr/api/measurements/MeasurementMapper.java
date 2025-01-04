package it.mulders.traqqr.api.measurements;

import it.mulders.traqqr.api.measurements.dto.MeasurementDto;
import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface MeasurementMapper {
    Measurement toMeasurement(Vehicle vehicle, MeasurementDto measurementDto);
}
