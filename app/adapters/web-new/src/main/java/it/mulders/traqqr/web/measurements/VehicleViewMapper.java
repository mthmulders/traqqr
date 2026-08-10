package it.mulders.traqqr.web.measurements;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.web.measurements.model.VehicleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.JAKARTA_CDI,
        nullValueIterableMappingStrategy = RETURN_DEFAULT,
        injectionStrategy = CONSTRUCTOR)
public interface VehicleViewMapper {
    VehicleDTO vehicleToDto(final Vehicle vehicle);
}
