package it.mulders.traqqr.domain.measurements;

import it.mulders.traqqr.domain.measurements.api.RegisterMeasurementService;
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
        var maybeVehicle = vehicleRepository.findByCode(vehicleCode);

        if (maybeVehicle.isEmpty()) {
            logger.info("Registering measurement failed, unknown vehicle; vehicle_code={}", vehicleCode);
            return RegisterMeasurementOutcome.UNKNOWN_VEHICLE;
        }

        var vehicle = maybeVehicle.get();

        if (apiKey == null || !vehicle.hasAuthorisationWithKey(apiKey)) {
            logger.info("Registering measurement failed, invalid API key; vehicle_code={}", vehicleCode);
            return RegisterMeasurementOutcome.UNAUTHORIZED;
        }

        measurementRepository.save(measurement
                .withRegistrationTimestamp(OffsetDateTime.now())
                .withSource(Source.API)
                .withVehicle(vehicle));

        return RegisterMeasurementOutcome.SUCCESS;
    }
}
