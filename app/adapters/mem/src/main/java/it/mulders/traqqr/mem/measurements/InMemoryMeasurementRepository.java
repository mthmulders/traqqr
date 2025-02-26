package it.mulders.traqqr.mem.measurements;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.MeasurementRepository;
import it.mulders.traqqr.domain.shared.Pagination;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@ApplicationScoped
public class InMemoryMeasurementRepository implements MeasurementRepository {
    private final Set<Measurement> measurements = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void save(Measurement measurement) {
        measurements.add(measurement);
    }

    @Override
    public Collection<Measurement> findByVehicle(Vehicle vehicle) {
        return measurements.stream().filter(byVehicle(vehicle)).collect(Collectors.toList());
    }

    @Override
    public Collection<Measurement> findByVehicle(Vehicle vehicle, Pagination pagination) {
        return measurements.stream()
                .filter(byVehicle(vehicle))
                .skip(pagination.offset())
                .limit(pagination.limit())
                .collect(Collectors.toList());
    }

    @Override
    public long countByVehicle(Vehicle vehicle) {
        return measurements.stream().filter(byVehicle(vehicle)).count();
    }

    private Predicate<Measurement> byVehicle(Vehicle vehicle) {
        return measurement -> measurement.vehicle().code().equals(vehicle.code());
    }

    @Override
    public void removeMeasurement(Measurement measurement) {
        measurements.remove(measurement);
    }
}
