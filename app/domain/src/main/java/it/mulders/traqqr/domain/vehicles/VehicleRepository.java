package it.mulders.traqqr.domain.vehicles;

import it.mulders.traqqr.domain.user.Owner;
import java.util.Collection;
import java.util.Optional;

/**
 * Describes operations for working with persistent vehicle objects.
 */
public interface VehicleRepository {
    public Optional<Vehicle> findByCode(final String code);

    public Collection<Vehicle> findByOwnerId(final Owner owner);

    public void save(final Vehicle vehicle);

    public void update(final Vehicle vehicle);
}
