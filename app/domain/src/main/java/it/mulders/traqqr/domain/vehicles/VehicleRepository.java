package it.mulders.traqqr.domain.vehicles;

import it.mulders.traqqr.domain.user.Owner;
import java.util.Collection;
import java.util.Optional;

/**
 * Describes operations for working with persistent vehicle objects.
 */
public interface VehicleRepository {
    Optional<Vehicle> findByCode(final String code);

    Collection<Vehicle> findByOwnerId(final Owner owner);

    void save(final Vehicle vehicle);

    void update(final Vehicle vehicle);

    void removeVehicle(final Vehicle vehicle);
}
