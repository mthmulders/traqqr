package it.mulders.traqqr.api.measurements;

import it.mulders.traqqr.api.measurements.dto.MeasurementDto;
import it.mulders.traqqr.domain.measurements.Measurement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface MeasurementMapper {
    @Mapping(source = "measurementDto.timestamp", target = "measurementTimestamp")
    @Mapping(expression = "java(it.mulders.traqqr.domain.measurements.Source.API)", target = "source")
    Measurement toMeasurement(MeasurementDto measurementDto);
}
