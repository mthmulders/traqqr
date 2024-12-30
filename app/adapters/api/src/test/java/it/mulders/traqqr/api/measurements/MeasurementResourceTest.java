package it.mulders.traqqr.api.measurements;

import it.mulders.traqqr.api.measurements.dto.MeasurementDto;
import it.mulders.traqqr.domain.vehicles.Authorisation;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.mem.measurements.InMemoryMeasurementRepository;
import it.mulders.traqqr.mem.vehicles.InMemoryVehicleRepository;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;

public class MeasurementResourceTest {
    private MeasurementResource resource;
    private InMemoryMeasurementRepository measurementRepository;
    private InMemoryVehicleRepository vehicleRepository;
    private MeasurementMapper measurementMapper;

    @BeforeEach
    public void setUp() {
        measurementRepository = new InMemoryMeasurementRepository();
        vehicleRepository = new InMemoryVehicleRepository();
        measurementMapper = new MeasurementMapperImpl();
        resource = new MeasurementResource(measurementRepository, vehicleRepository, measurementMapper);
    }

    @Test
    public void testRegisterMeasurement_Success() {
        var vehicle = new Vehicle("code123", "description", "ownerId", Set.of(Authorisation.fromInput("hashedKey123")));
        vehicleRepository.save(vehicle);

        var measurementDto = new MeasurementDto(
                OffsetDateTime.now(),
                1000,
                new MeasurementDto.BatteryDto((byte) 80),
                new MeasurementDto.LocationDto(52.0, 4.0)
        );

        var headers = new ResteasyHttpHeaders(
            new MultivaluedHashMap<>(Map.of("X-VEHICLE-API-KEY", "hashedKey123"))
        );

        var response = resource.registerMeasurement("code123", measurementDto, headers);
        Assertions.assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());
        Assertions.assertThat(measurementRepository.findByVehicle(vehicle)).isNotNull();
    }

    @Test
    public void testRegisterMeasurement_VehicleNotFound() {
        var measurementDto = new MeasurementDto(
                OffsetDateTime.now(),
                1000,
                new MeasurementDto.BatteryDto((byte) 80),
                new MeasurementDto.LocationDto(52.0, 4.0)
        );

        var headers = new ResteasyHttpHeaders(
            new MultivaluedHashMap<>(Map.of("X-VEHICLE-API-KEY", "hashedKey123"))
        );

        var response = resource.registerMeasurement("code123", measurementDto, headers);
        Assertions.assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testRegisterMeasurement_Unauthorized() {
        var vehicle = new Vehicle("code123", "description", "ownerId", null);
        vehicleRepository.save(vehicle);

        var measurementDto = new MeasurementDto(
                OffsetDateTime.now(),
                1000,
                new MeasurementDto.BatteryDto((byte) 80),
                new MeasurementDto.LocationDto(52.0, 4.0)
        );

        var headers = new ResteasyHttpHeaders(
            new MultivaluedHashMap<>(Map.of("X-VEHICLE-API-KEY", "invalidKey"))
        );

        var response = resource.registerMeasurement("code123", measurementDto, headers);
        Assertions.assertThat(response.getStatus()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
    }
}
