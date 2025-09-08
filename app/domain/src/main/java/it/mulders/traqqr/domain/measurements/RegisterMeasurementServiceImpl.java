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
            case VehicleFound(var vehicle) -> storeAutomatedMeasurement(measurement, vehicle, apiKey);
        };
    }

    @Override
    public RegisterMeasurementOutcome registerManualMeasurement(String vehicleCode, Measurement measurement) {
        return switch (lookupVehicle(vehicleCode)) {
            case VehicleNotFound ignored -> RegisterMeasurementOutcome.UNKNOWN_VEHICLE;
            case VehicleFound(var vehicle) -> storeManualMeasurement(measurement, vehicle);
        };
    }

    private RegisterMeasurementOutcome storeAutomatedMeasurement(Measurement measurement, Vehicle vehicle, String apiKey) {
        if (apiKey == null || !vehicle.hasAuthorisationWithKey(apiKey)) {
            logger.info("Registering measurement failed, invalid API key; vehicle_code={}", vehicle.code());
            return RegisterMeasurementOutcome.UNAUTHORIZED;
        }

        return storeMeasurement(measurement, Source.API, vehicle);
    }

    private RegisterMeasurementOutcome storeManualMeasurement(Measurement measurement, Vehicle vehicle) {
        return storeMeasurement(measurement, Source.USER, vehicle);
    }

    private RegisterMeasurementOutcome storeMeasurement(Measurement measurement, Source source, Vehicle vehicle) {
        measurementRepository.save(measurement
                .withRegistrationTimestamp(OffsetDateTime.now())
                .withSource(source)
                .withVehicle(vehicle));

        return RegisterMeasurementOutcome.SUCCESS;
    }

    private LookupVehicleOutcome lookupVehicle(String vehicleCode) {
        var maybeVehicle = vehicleRepository.findByCode(vehicleCode);

        if (maybeVehicle.isEmpty()) {
            logger.info("Registering measurement failed, unknown vehicle; vehicle_code={}", vehicleCode);
            return new VehicleNotFound();
        }

        return new VehicleFound(maybeVehicle.get());
    }

    private sealed interface LookupVehicleOutcome {}

    private record VehicleNotFound() implements LookupVehicleOutcome {}

    private record VehicleFound(Vehicle vehicle) implements LookupVehicleOutcome {}
}
