package it.mulders.traqqr.domain.vehicles;

import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.spi.VehicleRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class InMemoryVehicleRepository implements VehicleRepository {
    private final Collection<Vehicle> vehicles;

    public InMemoryVehicleRepository() {
        this(new ArrayList<>());
    }

    public InMemoryVehicleRepository(Collection<Vehicle> vehicles) {
        this.vehicles = new ArrayList<>(vehicles);
    }

    @Override
    public Optional<Vehicle> findByCode(String code) {
        return vehicles.stream().filter(vehicle -> code.equals(vehicle.code())).findAny();
    }

    @Override
    public Collection<Vehicle> findByOwner(Owner owner) {
        return vehicles.stream()
                .filter(vehicle -> vehicle.ownerId().equals(owner.code()))
                .toList();
    }

    @Override
    public Optional<Vehicle> findByOwnerAndCode(Owner owner, String code) {
        return Optional.empty();
    }

    @Override
    public void save(Vehicle vehicle) {
        this.vehicles.add(vehicle);
    }

    @Override
    public void update(Vehicle vehicle) {
        // Intentionally left empty
    }

    @Override
    public void removeVehicle(Vehicle vehicle) {
        // Intentionally left empty
    }
}
