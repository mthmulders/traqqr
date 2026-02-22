package it.mulders.traqqr.web.vehicles;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.web.vehicles.model.VehicleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.JAKARTA_CDI,
        nullValueIterableMappingStrategy = RETURN_DEFAULT,
        injectionStrategy = CONSTRUCTOR)
public interface VehicleViewMapper {
    VehicleDTO vehicleToDto(final Vehicle vehicle);
}
