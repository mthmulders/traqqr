package it.mulders.traqqr.api.measurements;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.MeasurementRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InMemoryMeasurementRepository implements MeasurementRepository {
    private final Map<UUID, Measurement> measurements = new HashMap<>();

    @Override
    public void save(Measurement measurement) {
        measurements.put(measurement.id(), measurement);
    }
}
