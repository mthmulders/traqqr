package it.mulders.traqqr.api.measurements;

import static it.mulders.traqqr.domain.fakes.VehicleFaker.createVehicle;

import it.mulders.traqqr.api.measurements.dto.MeasurementDto;
import it.mulders.traqqr.mem.measurements.InMemoryMeasurementRepository;
import it.mulders.traqqr.mem.vehicles.InMemoryVehicleRepository;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.Response;
import java.time.OffsetDateTime;
import java.util.Map;
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

    private final MeasurementResource resource =
            new MeasurementResource(measurementMapper, measurementRepository, vehicleRepository);

    @Test
    void with_valid_key_for_vehicle_should_register_measurement() {
        var vehicle = createVehicle();
        var authorisation = vehicle.regenerateKey();
        vehicleRepository.save(vehicle);

        var measurementDto = new MeasurementDto(
                OffsetDateTime.now(),
                1000,
                new MeasurementDto.BatteryDto((byte) 80),
                new MeasurementDto.LocationDto(52.0, 4.0));

        var headers = new ResteasyHttpHeaders(
                new MultivaluedHashMap<>(Map.of("X-VEHICLE-API-KEY", authorisation.getRawKey())));

        var response = resource.registerMeasurement(vehicle.code(), measurementDto, headers);

        assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());
        assertThat(measurementRepository.findByVehicle(vehicle)).isNotNull().anySatisfy(measurement -> {
            assertThat(measurement.vehicle().code()).isEqualTo(vehicle.code());
            assertThat(measurement.odometer()).isEqualTo(measurementDto.odometer());
            assertThat(measurement.battery().soc())
                    .isEqualTo(measurementDto.battery().soc());
        });
    }

    @Test
    void with_non_existing_vehicle_should_not_register_measurement() {
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
    void with_invalid_key_for_vehicle_should_not_register_measurement() {
        var vehicle = createVehicle();
        vehicleRepository.save(vehicle);

        var measurementDto = new MeasurementDto(
                OffsetDateTime.now(),
                1000,
                new MeasurementDto.BatteryDto((byte) 80),
                new MeasurementDto.LocationDto(52.0, 4.0));

        var headers = new ResteasyHttpHeaders(new MultivaluedHashMap<>(Map.of("X-VEHICLE-API-KEY", "invalidKey")));

        var response = resource.registerMeasurement(vehicle.code(), measurementDto, headers);
        assertThat(response.getStatus()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    void without_key_should_not_register_measurement() {
        var vehicle = createVehicle();
        vehicleRepository.save(vehicle);

        var measurementDto = new MeasurementDto(
                OffsetDateTime.now(),
                1000,
                new MeasurementDto.BatteryDto((byte) 80),
                new MeasurementDto.LocationDto(52.0, 4.0));

        var headers = new ResteasyHttpHeaders(new MultivaluedHashMap<>(Map.of()));

        var response = resource.registerMeasurement(vehicle.code(), measurementDto, headers);
        assertThat(response.getStatus()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
    }
}
