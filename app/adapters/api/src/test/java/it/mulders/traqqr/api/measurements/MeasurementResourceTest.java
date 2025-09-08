package it.mulders.traqqr.api.measurements;

import it.mulders.traqqr.api.measurements.dto.MeasurementDto;
import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.api.RegisterMeasurementService;
import it.mulders.traqqr.domain.shared.RandomStringUtils;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.Response;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.WithAssertions;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class MeasurementResourceTest implements WithAssertions {
    private final MeasurementMapper measurementMapper = new MeasurementMapperImpl();
    private final TestRegisterMeasurementService registerMeasurementService = new TestRegisterMeasurementService();

    private static class TestRegisterMeasurementService implements RegisterMeasurementService {
        private final Map<String, Collection<Measurement>> measurements = new HashMap<>();

        @Override
        public RegisterMeasurementOutcome registerAutomatedMeasurement(
                String vehicleCode, String apiKey, Measurement measurement) {
            if ("non-existing-vehicle".equals(vehicleCode)) {
                return RegisterMeasurementOutcome.UNKNOWN_VEHICLE;
            } else if ("invalid-key".equals(apiKey)) {
                return RegisterMeasurementOutcome.UNAUTHORIZED;
            } else {
                measurements.putIfAbsent(vehicleCode, new ArrayList<>());
                measurements.get(vehicleCode).add(measurement);
                return RegisterMeasurementOutcome.SUCCESS;
            }
        }

        @Override
        public RegisterMeasurementOutcome registerManualMeasurement(String vehicleCode, Measurement measurement) {
            throw new IllegalStateException();
        }

        public Map<String, Collection<Measurement>> getMeasurements() {
            return measurements;
        }
    }
    ;

    private final MeasurementResource resource = new MeasurementResource(measurementMapper, registerMeasurementService);

    @Test
    void with_valid_key_for_vehicle_should_register_measurement() {
        // Arrange
        var vehicleCode = RandomStringUtils.generateRandomIdentifier(8);
        var measurementDto = new MeasurementDto(
                OffsetDateTime.now(),
                1_000,
                new MeasurementDto.BatteryDto((byte) 80),
                new MeasurementDto.LocationDto(52.0, 4.0));
        var headers = new ResteasyHttpHeaders(new MultivaluedHashMap<>(Map.of("X-VEHICLE-API-KEY", "whatever")));

        // Act
        var response = resource.registerMeasurement(vehicleCode, measurementDto, headers);

        // Assert
        assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());
        assertThat(registerMeasurementService.getMeasurements().get(vehicleCode))
                .isNotNull()
                .singleElement()
                .isInstanceOf(Measurement.class);
    }

    @Test
    void with_non_existing_vehicle_should_not_register_measurement() {
        // Arrange
        var vehicleCode = "non-existing-vehicle";
        var measurementDto = new MeasurementDto(
                OffsetDateTime.now(),
                1_000,
                new MeasurementDto.BatteryDto((byte) 80),
                new MeasurementDto.LocationDto(52.0, 4.0));
        var headers = new ResteasyHttpHeaders(new MultivaluedHashMap<>(Map.of("X-VEHICLE-API-KEY", "hashedKey123")));

        // Act
        var response = resource.registerMeasurement(vehicleCode, measurementDto, headers);

        // Assert
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        assertThat(registerMeasurementService.getMeasurements().get(vehicleCode))
                .isNullOrEmpty();
    }

    @Test
    void with_invalid_key_for_vehicle_should_not_register_measurement() {
        // Arrange
        var vehicleCode = RandomStringUtils.generateRandomIdentifier(8);
        var measurementDto = new MeasurementDto(
                OffsetDateTime.now(),
                1000,
                new MeasurementDto.BatteryDto((byte) 80),
                new MeasurementDto.LocationDto(52.0, 4.0));
        var headers = new ResteasyHttpHeaders(new MultivaluedHashMap<>(Map.of("X-VEHICLE-API-KEY", "invalid-key")));

        // Act
        var response = resource.registerMeasurement(vehicleCode, measurementDto, headers);

        // Assert
        assertThat(response.getStatus()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
        assertThat(registerMeasurementService.getMeasurements().get(vehicleCode))
                .isNullOrEmpty();
    }
}
