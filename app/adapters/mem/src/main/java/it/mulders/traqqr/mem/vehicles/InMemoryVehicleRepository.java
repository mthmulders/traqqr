package it.mulders.traqqr.mem.vehicles;

import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Named
public class InMemoryVehicleRepository implements VehicleRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryVehicleRepository.class);

    private final Set<Vehicle> vehicles = new HashSet<>();

    @Override
    public Optional<Vehicle> findByCode(final String code) {
        return vehicles.stream().filter(vehicle -> code.equals(vehicle.code())).findAny();
    }

    @Override
    public void save(final Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    @Override
    public void update(final Vehicle vehicle) {
        findByCode(vehicle.code())
                .ifPresentOrElse(
                        existing -> {
                            log.debug("Updating vehicle; code={}", existing.code());
                            vehicles.remove(existing);
                            vehicles.add(vehicle);
                        },
                        handleVehicleNotFound(vehicle));
    }

    @Override
    public void removeVehicle(Vehicle vehicle) {
        findByCode(vehicle.code())
                .ifPresentOrElse(
                        existing -> {
                            log.debug("Removing vehicle; code={}", existing.code());
                            vehicles.remove(existing);
                        },
                        handleVehicleNotFound(vehicle));
    }

    private Runnable handleVehicleNotFound(final Vehicle vehicle) {
        return () -> log.error("Vehicle not found; code={}", vehicle.code());
    }

    @Override
    public Collection<Vehicle> findByOwner(final Owner owner) {
        return vehicles.stream()
                .filter(vehicle -> owner.code().equals(vehicle.ownerId()))
                .collect(Collectors.toList());
    }
}
