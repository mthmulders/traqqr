package it.mulders.traqqr.jpa.measurements;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.MeasurementRepository;
import it.mulders.traqqr.domain.measurements.Source;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.jpa.AbstractJpaRepositoryTest;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class JpaMeasurementRepositoryIT extends AbstractJpaRepositoryTest<MeasurementRepository, JpaMeasurementRepository> {
    @BeforeEach
    void prepare() {
        prepare(em -> new JpaMeasurementRepository(em, measurementMapper));
    }

    @Test
    void should_save_measurement() {
        // Arrange
        var vehicle = createVehicle("000000");
        persist(vehicleMapper.vehicleToVehicleEntity(vehicle));
        var measurement = createMeasurement(vehicle);

        // Act
        runTransactional(() -> repository.save(measurement));

        // Assert
        assertThat(repository.findByVehicle(vehicle)).hasSize(1).allSatisfy(found -> {
            assertThat(found.measurementTimestamp()).isEqualTo(measurement.measurementTimestamp());
            assertThat(found.registrationTimestamp()).isEqualTo(measurement.registrationTimestamp());
            assertThat(found.odometer()).isEqualTo(measurement.odometer());
            assertThat(found.location()).isEqualTo(measurement.location());
            assertThat(found.battery()).isEqualTo(measurement.battery());
        });
    }

    @Test
    void should_save_measurement_without_id() {
        // Arrange
        var vehicle = createVehicle("000001");
        persist(vehicleMapper.vehicleToVehicleEntity(vehicle));
        var measurement = new Measurement(
                null,
                OffsetDateTime.now().minusDays(1),
                OffsetDateTime.now().minusDays(1).minusSeconds(5),
                1_000,
                new Measurement.Battery((byte) 80),
                new Measurement.Location(55.0, 6.0),
                Source.API,
                vehicle);

        // Act
        runTransactional(() -> repository.save(measurement));

        // Assert
        assertThat(repository.findByVehicle(vehicle)).hasSize(1).allSatisfy(found -> {
            assertThat(found.measurementTimestamp()).isEqualTo(measurement.measurementTimestamp());
            assertThat(found.registrationTimestamp()).isEqualTo(measurement.registrationTimestamp());
            assertThat(found.odometer()).isEqualTo(measurement.odometer());
            assertThat(found.location()).isEqualTo(measurement.location());
            assertThat(found.battery()).isEqualTo(measurement.battery());
        });
    }

    @Test
    void should_find_measurements_for_vehicle() {
        // Arrange
        var vehicle = createVehicle("000002");
        persist(vehicleMapper.vehicleToVehicleEntity(vehicle));
        var measurement1 = createMeasurement(vehicle);
        var measurement2 = createMeasurement(vehicle);

        runTransactional(() -> {
            repository.save(measurement1);
            repository.save(measurement2);
        });

        // Act
        var measurements = repository.findByVehicle(vehicle);

        // Assert
        assertThat(measurements).hasSize(2).allSatisfy(found -> {
            assertThat(found.vehicle().code()).isEqualTo(vehicle.code());
        });
    }

    @Test
    void should_count_measurements_for_vehicle() {
        // Arrange
        var vehicle = createVehicle("000003");
        persist(vehicleMapper.vehicleToVehicleEntity(vehicle));
        var measurement = createMeasurement(vehicle);

        runTransactional(() -> repository.save(measurement));

        // Act
        var result = repository.countByVehicle(vehicle);

        // Assert
        assertThat(result).isEqualTo(1);
    }

    private Measurement createMeasurement(Vehicle vehicle) {
        return new Measurement(
                UUID.randomUUID(),
                OffsetDateTime.now(),
                OffsetDateTime.now().minus(5, ChronoUnit.SECONDS),
                1_000,
                new Measurement.Battery((byte) 80),
                new Measurement.Location(55.0, 6.0),
                Source.API,
                vehicle);
    }
}
