package it.mulders.traqqr.mem.measurements;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.MeasurementRepository;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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
        return measurements.stream()
                .filter(measurement -> measurement.vehicle().equals(vehicle))
                .collect(Collectors.toList());
    }
}
