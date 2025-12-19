package it.mulders.traqqr.domain.measurements.spi;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.shared.Pagination;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import java.util.Collection;
import java.util.stream.Stream;

public interface MeasurementRepository {
    void save(Measurement measurement);

    Collection<Measurement> findByVehicle(Vehicle vehicle);

    Collection<Measurement> findByVehicle(Vehicle vehicle, Pagination pagination);

    Stream<Measurement> exampleStreamingFindForBatchJob();

    Collection<Measurement> findOldestMeasurementsWithoutLocationDescription();

    long countByVehicle(Vehicle vehicle);

    void removeMeasurement(final Measurement measurement);
}
