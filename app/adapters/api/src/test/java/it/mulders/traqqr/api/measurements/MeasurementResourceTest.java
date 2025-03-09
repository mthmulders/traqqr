package it.mulders.traqqr.api.measurements;

import it.mulders.traqqr.api.measurements.dto.MeasurementDto;
import it.mulders.traqqr.domain.vehicles.Authorisation;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.mem.measurements.InMemoryMeasurementRepository;
import it.mulders.traqqr.mem.vehicles.InMemoryVehicleRepository;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import org.assertj.core.api.WithAssertions;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class MeasurementResourceTest implements WithAssertions {
    private final InMemoryMeasurementRepository measurementRepository = new InMemoryMeasurementRepository();
    private final InMemoryVehicleRepository vehicleRepository = new InMemoryVehicleRepository();
    private final MeasurementMapper measurementMapper = new MeasurementMapperImpl();

    private final MeasurementResource resource = new MeasurementResource(measurementMapper, measurementRepository, vehicleRepository);

    @Test
    void testRegisterMeasurement_Success() {
        var vehicle = new Vehicle(
                "code123",
                "description",
                "ownerId",
                Set.of(Authorisation.fromInput("hashedKey123")),
                BigDecimal.valueOf(50.0));
        vehicleRepository.save(vehicle);

        var measurementDto = new MeasurementDto(
                OffsetDateTime.now(),
                1000,
                new MeasurementDto.BatteryDto((byte) 80),
                new MeasurementDto.LocationDto(52.0, 4.0));

        var headers = new ResteasyHttpHeaders(new MultivaluedHashMap<>(Map.of("X-VEHICLE-API-KEY", "hashedKey123")));

        var response = resource.registerMeasurement("code123", measurementDto, headers);

        assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());
        assertThat(measurementRepository.findByVehicle(vehicle)).isNotNull().anySatisfy(measurement -> {
            assertThat(measurement.vehicle().code()).isEqualTo(vehicle.code());
            assertThat(measurement.odometer()).isEqualTo(measurementDto.odometer());
            assertThat(measurement.battery().soc())
                    .isEqualTo(measurementDto.battery().soc());
        });
    }

    @Test
    void testRegisterMeasurement_VehicleNotFound() {
        var measurementDto = new MeasurementDto(
                OffsetDateTime.now(),
                1000,
                new MeasurementDto.BatteryDto((byte) 80),
                new MeasurementDto.LocationDto(52.0, 4.0));

        var headers = new ResteasyHttpHeaders(new MultivaluedHashMap<>(Map.of("X-VEHICLE-API-KEY", "hashedKey123")));

        var response = resource.registerMeasurement("code123", measurementDto, headers);
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void testRegisterMeasurement_Unauthorized() {
        var vehicle = new Vehicle("code123", "description", "ownerId", null, BigDecimal.valueOf(50.0));
        vehicleRepository.save(vehicle);

        var measurementDto = new MeasurementDto(
                OffsetDateTime.now(),
                1000,
                new MeasurementDto.BatteryDto((byte) 80),
                new MeasurementDto.LocationDto(52.0, 4.0));

        var headers = new ResteasyHttpHeaders(new MultivaluedHashMap<>(Map.of("X-VEHICLE-API-KEY", "invalidKey")));

        var response = resource.registerMeasurement("code123", measurementDto, headers);
        assertThat(response.getStatus()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
    }
}
