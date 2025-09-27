package it.mulders.traqqr.domain.vehicles.spi;

import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import java.util.Collection;
import java.util.Optional;

/**
 * Describes operations for working with persistent vehicle objects.
 */
public interface VehicleRepository {
    Optional<Vehicle> findByCode(final String code);

    Collection<Vehicle> findByOwner(final Owner owner);

    Optional<Vehicle> findByOwnerAndCode(final Owner owner, final String code);

    void save(final Vehicle vehicle);

    void update(final Vehicle vehicle);

    void removeVehicle(final Vehicle vehicle);
}
