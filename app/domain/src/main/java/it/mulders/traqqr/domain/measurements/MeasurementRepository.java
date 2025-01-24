package it.mulders.traqqr.domain.measurements;

import it.mulders.traqqr.domain.shared.Pagination;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import java.util.Collection;

public interface MeasurementRepository {
    void save(Measurement measurement);

    Collection<Measurement> findByVehicle(Vehicle vehicle);

    Collection<Measurement> findByVehicle(Vehicle vehicle, Pagination pagination);

    long countByVehicle(Vehicle vehicle);

    void removeMeasurement(final Measurement measurement);
}
