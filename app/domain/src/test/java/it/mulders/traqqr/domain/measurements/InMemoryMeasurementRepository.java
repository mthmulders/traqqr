package it.mulders.traqqr.domain.measurements;

import static java.util.Comparator.comparing;

import it.mulders.traqqr.domain.measurements.spi.MeasurementRepository;
import it.mulders.traqqr.domain.shared.Pagination;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import java.util.*;
import java.util.stream.Stream;

public class InMemoryMeasurementRepository implements MeasurementRepository {
    private final Set<Measurement> measurements = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void save(Measurement measurement) {
        measurements.add(measurement);
    }

    @Override
    public Collection<Measurement> findByVehicle(Vehicle vehicle) {
        return measurements.stream()
                .filter(measurement -> measurement.vehicle().code().equals(vehicle.code()))
                .toList();
    }

    @Override
    public Collection<Measurement> findByVehicle(Vehicle vehicle, Pagination pagination) {
        return List.of();
    }

    @Override
    public Stream<Measurement> exampleStreamingFindForBatchJob() {
        return Stream.empty();
    }

    @Override
    public long countByVehicle(Vehicle vehicle) {
        return measurements.stream().filter(m -> m.vehicle().equals(vehicle)).count();
    }

    @Override
    public void removeMeasurement(Measurement measurement) {
        // Intentionally left empty
    }

    @Override
    public Collection<Measurement> findOldestMeasurementsWithoutLocationDescription() {
        return measurements.stream()
                .filter(m -> m.location() != null)
                .filter(m -> m.location().description() == null
                        || m.location().description().isEmpty())
                .sorted(comparing(Measurement::measurementTimestamp))
                .limit(100)
                .toList();
    }
}
