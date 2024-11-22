package it.mulders.traqqr.api.measurements;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.MeasurementRepository;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import java.util.UUID;

@RequestScoped
@Path("/v1/vehicle/{code}/measurement")
public class MeasurementResource {
    private final MeasurementRepository measurementRepository;
    private final VehicleRepository vehicleRepository;
    private final MeasurementMapper measurementMapper;

    @Inject
    public MeasurementResource(MeasurementRepository measurementRepository, VehicleRepository vehicleRepository, MeasurementMapper measurementMapper) {
        this.measurementRepository = measurementRepository;
        this.vehicleRepository = vehicleRepository;
        this.measurementMapper = measurementMapper;
    }

    @POST
    public Response registerMeasurement(@PathParam("code") String code, MeasurementDto measurementDto, @Context HttpHeaders headers) {
        String apiKey = headers.getHeaderString("X-VEHICLE-API-KEY");
        Vehicle vehicle = vehicleRepository.findByCode(code)
                .filter(v -> v.hasAuthorisationWithHashedKey(apiKey))
                .orElse(null);

        if (vehicle == null) {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        Measurement measurement = measurementMapper.toMeasurement(measurementDto);

        measurementRepository.save(measurement);
        return Response.status(Status.CREATED).build();
    }
}
