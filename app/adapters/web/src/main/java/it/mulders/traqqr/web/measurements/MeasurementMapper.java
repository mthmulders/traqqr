package it.mulders.traqqr.web.measurements;

import static java.lang.Double.parseDouble;
import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.JAKARTA_CDI;
import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.Source;
import it.mulders.traqqr.web.measurements.model.MeasurementDTO;
import java.time.OffsetDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = JAKARTA_CDI,
        nullValueIterableMappingStrategy = RETURN_DEFAULT,
        injectionStrategy = CONSTRUCTOR)
public interface MeasurementMapper {
    @Mapping(target = "battery.soc", source = "measurementDto.batterySoc")
    Measurement measurementDtoToMeasurement(
            MeasurementDTO measurementDto,
            OffsetDateTime measurementTimestamp,
            OffsetDateTime registrationTimestamp,
            Source source);

    default Measurement.Location map(String coordinates) {
        if (coordinates == null || coordinates.isEmpty()) {
            return null;
        }

        var parts = coordinates.split(",");
        return new Measurement.Location(parseDouble(parts[0].trim()), parseDouble(parts[1].trim()));
    }
}
