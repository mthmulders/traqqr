package it.mulders.traqqr.api.measurements;

import it.mulders.traqqr.domain.measurements.Measurement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta")
public interface MeasurementMapper {
    @Mapping(target = "vehicle", ignore = true)
    Measurement toMeasurement(MeasurementDto measurementDto);
}
