package it.mulders.traqqr.api.measurements;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.MeasurementRepository;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MeasurementResourceTest {
    @Mock
    private MeasurementRepository measurementRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private HttpHeaders headers;

    @InjectMocks
    private MeasurementResource measurementResource;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterMeasurement() {
        String code = "vehicleCode";
        String apiKey = "validApiKey";
        Vehicle vehicle = mock(Vehicle.class);
        MeasurementDto measurementDto = new MeasurementDto(
                OffsetDateTime.now(), 1000, new MeasurementDto.Battery(80), new MeasurementDto.Location(52.0, 4.0));

        when(headers.getHeaderString("X-VEHICLE-API-KEY")).thenReturn(apiKey);
        when(vehicleRepository.findByCode(code)).thenReturn(Optional.of(vehicle));
        when(vehicle.hasAuthorisationWithHashedKey(apiKey)).thenReturn(true);

        Response response = measurementResource.registerMeasurement(code, measurementDto, headers);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(measurementRepository, times(1)).save(any(Measurement.class));
    }

    @Test
    public void testInvalidApiKey() {
        String code = "vehicleCode";
        String apiKey = "invalidApiKey";
        MeasurementDto measurementDto = new MeasurementDto(
                OffsetDateTime.now(), 1000, new MeasurementDto.Battery(80), new MeasurementDto.Location(52.0, 4.0));

        when(headers.getHeaderString("X-VEHICLE-API-KEY")).thenReturn(apiKey);
        when(vehicleRepository.findByCode(code)).thenReturn(Optional.empty());

        Response response = measurementResource.registerMeasurement(code, measurementDto, headers);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        verify(measurementRepository, never()).save(any(Measurement.class));
    }
}
