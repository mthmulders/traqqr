package it.mulders.traqqr.domain.measurements;

import it.mulders.traqqr.domain.vehicles.Vehicle;
import java.util.Collection;

public interface MeasurementRepository {
    void save(Measurement measurement);
    Collection<Measurement> findByVehicle(Vehicle vehicle);
}
