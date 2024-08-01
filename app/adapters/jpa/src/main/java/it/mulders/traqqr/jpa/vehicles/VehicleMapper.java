package it.mulders.traqqr.jpa.vehicles;

import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

import it.mulders.traqqr.domain.vehicles.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, nullValueIterableMappingStrategy = RETURN_DEFAULT)
public interface VehicleMapper {
    public Vehicle vehicleEntityToVehicle(final VehicleEntity entity);

    public VehicleEntity vehicleToVehicleEntity(final Vehicle vehicle);
}
