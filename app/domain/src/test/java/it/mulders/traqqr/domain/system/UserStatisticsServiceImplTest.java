package it.mulders.traqqr.domain.system;

import static org.assertj.core.api.Assertions.assertThat;

import it.mulders.traqqr.domain.measurements.InMemoryMeasurementRepository;
import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.Source;
import it.mulders.traqqr.domain.measurements.spi.MeasurementRepository;
import it.mulders.traqqr.domain.shared.RandomStringUtils;
import it.mulders.traqqr.domain.system.api.UserStatisticsService;
import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.InMemoryVehicleRepository;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.domain.vehicles.spi.VehicleRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserStatisticsServiceImplTest {
    private final MeasurementRepository measurementRepository = new InMemoryMeasurementRepository();
    private final VehicleRepository vehicleRepository = new InMemoryVehicleRepository();
    private final UserStatisticsService service =
            new UserStatisticsServiceImpl(measurementRepository, vehicleRepository);

    @Test
    void should_count_measurements_for_all_vehicles_of_owner() {
        // Arrange
        var vehicle1 = new Vehicle(
                RandomStringUtils.generateRandomIdentifier(8),
                "Test Vehicle 1",
                owner.code(),
                Collections.emptySet(),
                new BigDecimal(85));
        IntStream.range(0, 3)
                .mapToObj(i -> generateMeasurementForVehicle(vehicle1))
                .forEach(measurementRepository::save);
        var vehicle2 = new Vehicle(
                RandomStringUtils.generateRandomIdentifier(8),
                "Test Vehicle 2",
                owner.code(),
                Collections.emptySet(),
                new BigDecimal(85));
        IntStream.range(0, 8)
                .mapToObj(i -> generateMeasurementForVehicle(vehicle2))
                .forEach(measurementRepository::save);
        var vehicle3 = new Vehicle(
                RandomStringUtils.generateRandomIdentifier(8),
                "Test Vehicle 2",
                RandomStringUtils.generateRandomIdentifier(16),
                Collections.emptySet(),
                new BigDecimal(85));
        IntStream.range(0, 2)
                .mapToObj(i -> generateMeasurementForVehicle(vehicle3))
                .forEach(measurementRepository::save);
        vehicleRepository.save(vehicle1);
        vehicleRepository.save(vehicle2);
        vehicleRepository.save(vehicle3);

        // Act
        var result = service.countMeasurements(owner);

        // Assert
        assertThat(result).isEqualTo(11);
    }

    private Measurement generateMeasurementForVehicle(Vehicle vehicle) {
        return new Measurement(
                UUID.randomUUID(),
                OffsetDateTime.now(),
                OffsetDateTime.now().minusSeconds(3),
                0,
                new Measurement.Battery((byte) 80),
                new Measurement.Location(52.0, 13.0),
                Source.API,
                vehicle);
    }

    private final Owner owner = new Owner() {
        private final String code = RandomStringUtils.generateRandomIdentifier(16);

        @Override
        public String code() {
            return code;
        }

        @Override
        public String displayName() {
            return "Owner Display Name";
        }

        @Override
        public String profilePictureUrl() {
            return "https://example.com/profile-picture.jpg";
        }
    };
}
