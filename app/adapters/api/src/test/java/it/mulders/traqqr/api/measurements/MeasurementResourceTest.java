package it.mulders.traqqr.api.measurements;

import it.mulders.traqqr.domain.measurements.MeasurementRepository;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import it.mulders.traqqr.mem.vehicles.InMemoryVehicleRepository;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MeasurementResourceTest {
    private final MeasurementRepository measurementRepository = new InMemoryMeasurementRepository();
    private final VehicleRepository vehicleRepository = new InMemoryVehicleRepository();
    private final MeasurementMapper measurementMapper = Mappers.getMapper(MeasurementMapper.class);
    private final MeasurementResource measurementResource = new MeasurementResource(measurementRepository, vehicleRepository, measurementMapper);

    @Test
    public void testRegisterMeasurement() {
        String code = "vehicleCode";
        String apiKey = "validApiKey";
        Vehicle vehicle = new Vehicle(code, "Test Vehicle", "ownerId", Set.of(new Authorisation(apiKey)));
        vehicleRepository.save(vehicle);
        MeasurementDto measurementDto = new MeasurementDto(
                OffsetDateTime.now(), 1000, new MeasurementDto.Battery(80), new MeasurementDto.Location(52.0, 4.0));

        HttpHeaders headers = new HttpHeaders() {
            @Override
            public List<String> getRequestHeader(String name) {
                if ("X-VEHICLE-API-KEY".equals(name)) {
                    return List.of(apiKey);
                }
                return null;
            }

            @Override
            public String getHeaderString(String name) {
                if ("X-VEHICLE-API-KEY".equals(name)) {
                    return apiKey;
                }
                return null;
            }
        };

        Response response = measurementResource.registerMeasurement(code, measurementDto, headers);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void testInvalidApiKey() {
        String code = "vehicleCode";
        String apiKey = "invalidApiKey";
        MeasurementDto measurementDto = new MeasurementDto(
                OffsetDateTime.now(), 1000, new MeasurementDto.Battery(80), new MeasurementDto.Location(52.0, 4.0));

        HttpHeaders headers = new HttpHeaders() {
            @Override
            public List<String> getRequestHeader(String name) {
                if ("X-VEHICLE-API-KEY".equals(name)) {
                    return List.of(apiKey);
                }
                return null;
            }

            @Override
            public String getHeaderString(String name) {
                if ("X-VEHICLE-API-KEY".equals(name)) {
                    return apiKey;
                }
                return null;
            }
        };

        Response response = measurementResource.registerMeasurement(code, measurementDto, headers);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }
}
