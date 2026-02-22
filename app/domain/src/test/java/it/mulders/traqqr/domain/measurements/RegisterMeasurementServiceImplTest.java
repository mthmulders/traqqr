package it.mulders.traqqr.domain.measurements;

import it.mulders.traqqr.domain.measurements.Measurement.Battery;
import it.mulders.traqqr.domain.measurements.Measurement.Location;
import it.mulders.traqqr.domain.measurements.api.RegisterMeasurementService;
import it.mulders.traqqr.domain.measurements.api.RegisterMeasurementService.RegisterMeasurementOutcome;
import it.mulders.traqqr.domain.measurements.spi.MeasurementRepository;
import it.mulders.traqqr.domain.shared.RandomStringUtils;
import it.mulders.traqqr.domain.vehicles.Authorisation;
import it.mulders.traqqr.domain.vehicles.InMemoryVehicleRepository;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.domain.vehicles.spi.VehicleRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RegisterMeasurementServiceImplTest implements WithAssertions {
    private final Authorisation authorisation = Authorisation.generate();
    private final Vehicle vehicle = new Vehicle(
            RandomStringUtils.generateRandomIdentifier(8),
            "Test Vehicle",
            RandomStringUtils.generateRandomIdentifier(16),
            Collections.singleton(authorisation),
            new BigDecimal(85));
    private final MeasurementRepository measurementRepository = new InMemoryMeasurementRepository();
    private final VehicleRepository vehicleRepository = new InMemoryVehicleRepository(Collections.singleton(vehicle));
    private final RegisterMeasurementService service =
            new RegisterMeasurementServiceImpl(measurementRepository, vehicleRepository);

    @Test
    void should_fail_with_missing_API_key() {
        // Arrange
        var measurement = createMeasurement(
                OffsetDateTime.now().minusSeconds(5), 10_000, new Battery((byte) 75), new Location(0, 0));

        // Act
        var result = service.registerAutomatedMeasurement(vehicle.code(), null, measurement);

        // Assert
        assertThat(result).isEqualTo(RegisterMeasurementOutcome.UNAUTHORIZED);
    }

    @Test
    void should_fail_with_unknown_vehicle() {
        // Arrange
        var measurement = createMeasurement(
                OffsetDateTime.now().minusSeconds(5), 10_000, new Battery((byte) 75), new Location(0, 0));

        // Act
        var result = service.registerAutomatedMeasurement("non-existing-vehicle", null, measurement);

        // Assert
        assertThat(result).isEqualTo(RegisterMeasurementOutcome.UNKNOWN_VEHICLE);
    }

    @Test
    void should_fail_with_incorrect_API_key() {
        // Arrange
        var measurement = createMeasurement(
                OffsetDateTime.now().minusSeconds(5), 10_000, new Battery((byte) 75), new Location(0, 0));

        // Act
        var result = service.registerAutomatedMeasurement(vehicle.code(), "invalid-key", measurement);

        // Assert
        assertThat(result).isEqualTo(RegisterMeasurementOutcome.UNAUTHORIZED);
    }

    @Test
    void should_register_automated_measurement() {
        // Arrange
        var measurement = createMeasurement(
                OffsetDateTime.now().minusSeconds(5), 10_000, new Battery((byte) 75), new Location(0, 0));

        // Act
        var result = service.registerAutomatedMeasurement(vehicle.code(), authorisation.getRawKey(), measurement);

        // Assert
        assertThat(result).isEqualTo(RegisterMeasurementOutcome.SUCCESS);
    }

    @Test
    void should_link_automated_measurement_to_vehicle() {
        // Arrange
        var location = new Location(52.320418, 4.7685652);
        var measurement =
                createMeasurement(OffsetDateTime.now().minusSeconds(5), 10_000, new Battery((byte) 75), location);

        // Act
        service.registerAutomatedMeasurement(vehicle.code(), authorisation.getRawKey(), measurement);

        // Assert
        var measurements = measurementRepository.findByVehicle(vehicle);
        assertThat(measurements).anySatisfy(found -> {
            assertThat(found.vehicle().code()).isEqualTo(vehicle.code());
            assertThat(found.location()).isEqualTo(location);
        });
    }

    @Test
    void should_force_source_to_be_API() {
        // Arrange
        var location = new Location(52.320418, 4.7685652);
        var measurement =
                createMeasurement(OffsetDateTime.now().minusSeconds(5), 10_000, new Battery((byte) 75), location);

        // Act
        service.registerAutomatedMeasurement(vehicle.code(), authorisation.getRawKey(), measurement);

        // Assert
        var measurements = measurementRepository.findByVehicle(vehicle);
        assertThat(measurements).anySatisfy(found -> {
            assertThat(found.vehicle().code()).isEqualTo(vehicle.code());
            assertThat(found.source()).isEqualTo(Source.API);
        });
    }

    @Test
    void should_store_timestamp_of_automated_registration() {
        // Arrange
        var location = new Location(52.320418, 4.7685652);
        var measurement =
                createMeasurement(OffsetDateTime.now().minusSeconds(5), 10_000, new Battery((byte) 75), location);

        // Act
        service.registerAutomatedMeasurement(vehicle.code(), authorisation.getRawKey(), measurement);

        // Assert
        var measurements = measurementRepository.findByVehicle(vehicle);
        assertThat(measurements).anySatisfy(found -> {
            assertThat(found.vehicle().code()).isEqualTo(vehicle.code());
            assertThat(found.registrationTimestamp()).isInThePast();
        });
    }

    @Test
    void should_register_manual_measurement() {
        // Arrange
        var measurement = createMeasurement(
                OffsetDateTime.now().minusSeconds(5), 10_000, new Battery((byte) 75), new Location(0, 0));

        // Act
        var result = service.registerManualMeasurement(vehicle.code(), measurement);

        // Assert
        assertThat(result).isEqualTo(RegisterMeasurementOutcome.SUCCESS);
    }

    @Test
    void should_link_manual_measurement_to_vehicle() {
        // Arrange
        var location = new Location(52.320418, 4.7685652);
        var measurement =
                createMeasurement(OffsetDateTime.now().minusSeconds(5), 10_000, new Battery((byte) 75), location);

        // Act
        service.registerManualMeasurement(vehicle.code(), measurement);

        // Assert
        var measurements = measurementRepository.findByVehicle(vehicle);
        assertThat(measurements).anySatisfy(found -> {
            assertThat(found.vehicle().code()).isEqualTo(vehicle.code());
            assertThat(found.location()).isEqualTo(location);
        });
    }

    @Test
    void should_force_source_to_be_USER() {
        // Arrange
        var location = new Location(52.320418, 4.7685652);
        var measurement =
                createMeasurement(OffsetDateTime.now().minusSeconds(5), 10_000, new Battery((byte) 75), location);

        // Act
        service.registerManualMeasurement(vehicle.code(), measurement);

        // Assert
        var measurements = measurementRepository.findByVehicle(vehicle);
        assertThat(measurements).anySatisfy(found -> {
            assertThat(found.vehicle().code()).isEqualTo(vehicle.code());
            assertThat(found.source()).isEqualTo(Source.USER);
        });
    }

    @Test
    void should_store_timestamp_of_manual_registration() {
        // Arrange
        var location = new Location(52.320418, 4.7685652);
        var measurement =
                createMeasurement(OffsetDateTime.now().minusSeconds(5), 10_000, new Battery((byte) 75), location);

        // Act
        service.registerManualMeasurement(vehicle.code(), measurement);

        // Assert
        var measurements = measurementRepository.findByVehicle(vehicle);
        assertThat(measurements).anySatisfy(found -> {
            assertThat(found.vehicle().code()).isEqualTo(vehicle.code());
            assertThat(found.registrationTimestamp()).isInThePast();
        });
    }

    private Measurement createMeasurement(
            OffsetDateTime measurementTimestamp, int odometer, Battery battery, Location location) {
        return new Measurement(null, null, measurementTimestamp, odometer, battery, location, null, null);
    }
}
