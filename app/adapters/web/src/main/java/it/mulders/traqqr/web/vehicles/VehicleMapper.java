package it.mulders.traqqr.web.vehicles;

import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.Authorisation;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.web.vehicles.model.AuthorisationDTO;
import it.mulders.traqqr.web.vehicles.model.VehicleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.JAKARTA_CDI, nullValueIterableMappingStrategy = RETURN_DEFAULT)
public interface VehicleMapper {
    @Mapping(target = "authorisation", ignore = true)
    @Mapping(target = "netBatteryCapacity", source = "netBatteryCapacity")
    VehicleDTO vehicleToDto(final Vehicle vehicle);

    @Mapping(target = "ownerId", expression = "java(owner.code())")
    @Mapping(target = "authorisations", expression = "java(java.util.Collections.emptySet())")
    @Mapping(target = "netBatteryCapacity", source = "netBatteryCapacity")
    Vehicle vehicleDtoToVehicle(final VehicleDTO dto, final Owner owner);

    AuthorisationDTO authorisationToDto(final Authorisation authorisation);
}
