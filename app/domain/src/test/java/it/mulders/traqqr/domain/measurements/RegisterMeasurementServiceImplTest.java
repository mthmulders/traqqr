package it.mulders.traqqr.domain.measurements;

import it.mulders.traqqr.domain.measurements.Measurement.Battery;
import it.mulders.traqqr.domain.measurements.Measurement.Location;
import it.mulders.traqqr.domain.measurements.api.RegisterMeasurementService;
import it.mulders.traqqr.domain.measurements.api.RegisterMeasurementService.RegisterMeasurementOutcome;
import it.mulders.traqqr.domain.shared.Pagination;
import it.mulders.traqqr.domain.shared.RandomStringUtils;
import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.Authorisation;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
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
    void should_register_measurement() {
        // Arrange
        var measurement = createMeasurement(
                OffsetDateTime.now().minusSeconds(5), 10_000, new Battery((byte) 75), new Location(0, 0));

        // Act
        var result = service.registerAutomatedMeasurement(vehicle.code(), authorisation.getRawKey(), measurement);

        // Assert
        assertThat(result).isEqualTo(RegisterMeasurementOutcome.SUCCESS);
    }

    @Test
    void should_link_measurement_to_vehicle() {
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
    void should_store_timestamp_of_registration() {
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

    private Measurement createMeasurement(
            OffsetDateTime measurementTimestamp, int odometer, Battery battery, Location location) {
        return new Measurement(null, null, measurementTimestamp, odometer, battery, location, null, null);
    }

    private static class InMemoryVehicleRepository implements VehicleRepository {
        private final Collection<Vehicle> vehicles;

        public InMemoryVehicleRepository(Collection<Vehicle> vehicles) {
            this.vehicles = new ArrayList<>(vehicles);
        }

        @Override
        public Optional<Vehicle> findByCode(String code) {
            return vehicles.stream()
                    .filter(vehicle -> code.equals(vehicle.code()))
                    .findAny();
        }

        @Override
        public Collection<Vehicle> findByOwner(Owner owner) {
            return List.of();
        }

        @Override
        public Optional<Vehicle> findByOwnerAndCode(Owner owner, String code) {
            return Optional.empty();
        }

        @Override
        public void save(Vehicle vehicle) {}

        @Override
        public void update(Vehicle vehicle) {}

        @Override
        public void removeVehicle(Vehicle vehicle) {}
    }

    private static class InMemoryMeasurementRepository implements MeasurementRepository {
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
            return 0;
        }

        @Override
        public void removeMeasurement(Measurement measurement) {}
    }
}
