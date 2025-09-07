package it.mulders.traqqr.domain.measurements;

import it.mulders.traqqr.domain.measurements.api.RegisterMeasurementService;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class RegisterMeasurementServiceImpl implements RegisterMeasurementService {
    private final Logger logger = LoggerFactory.getLogger(RegisterMeasurementServiceImpl.class);

    // Components
    private final MeasurementRepository measurementRepository;
    private final VehicleRepository vehicleRepository;

    @Inject
    public RegisterMeasurementServiceImpl(
            MeasurementRepository measurementRepository, VehicleRepository vehicleRepository) {
        this.measurementRepository = measurementRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public RegisterMeasurementOutcome registerAutomatedMeasurement(
            String vehicleCode, String apiKey, Measurement measurement) {
        return switch (lookupVehicle(vehicleCode)) {
            case VehicleNotFound ignored -> RegisterMeasurementOutcome.UNKNOWN_VEHICLE;
            case VehicleFound found -> {
                var vehicle = found.vehicle();

                if (apiKey == null || !vehicle.hasAuthorisationWithKey(apiKey)) {
                    logger.info("Registering measurement failed, invalid API key; vehicle_code={}", vehicleCode);
                    yield RegisterMeasurementOutcome.UNAUTHORIZED;
                }

                yield storeMeasurement(measurement, Source.API, vehicle);
            }
        };
    }

    private sealed interface LookupVehicleOutcome {}

    private record VehicleNotFound() implements LookupVehicleOutcome {}

    private record VehicleFound(Vehicle vehicle) implements LookupVehicleOutcome {}
}
