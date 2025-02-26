package it.mulders.traqqr.jpa.vehicles;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

import it.mulders.traqqr.domain.vehicles.Authorisation;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(
        componentModel = ComponentModel.JAKARTA_CDI,
        nullValueIterableMappingStrategy = RETURN_DEFAULT,
        injectionStrategy = CONSTRUCTOR)
public interface VehicleMapper {
    Vehicle vehicleEntityToVehicle(final VehicleEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "netBatteryCapacity", target = "netBatteryCapacity")
    VehicleEntity vehicleToVehicleEntity(final Vehicle vehicle);

    Authorisation authorisationEntityToAuthorisation(AuthorisationEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    AuthorisationEntity authorisationToAuthorisationEntity(Authorisation authorisation);
}
