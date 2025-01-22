package it.mulders.traqqr.api.measurements;

import it.mulders.traqqr.api.measurements.dto.MeasurementDto;
import it.mulders.traqqr.domain.measurements.MeasurementRepository;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.time.OffsetDateTime;

@ApplicationScoped
@Path("/v1/vehicle/{code}/measurement")
public class MeasurementResource {
    @Inject
    private MeasurementRepository measurementRepository;

    @Inject
    private VehicleRepository vehicleRepository;

    @Inject
    private MeasurementMapper measurementMapper;

    public MeasurementResource() {}

    protected MeasurementResource(
            MeasurementRepository measurementRepository,
            VehicleRepository vehicleRepository,
            MeasurementMapper measurementMapper) {
        this.measurementRepository = measurementRepository;
        this.vehicleRepository = vehicleRepository;
        this.measurementMapper = measurementMapper;
    }

    @Consumes("application/json")
    @Produces("application/json")
    @POST
    @Transactional
    public Response registerMeasurement(
            @PathParam("code") String code, MeasurementDto measurementDto, @Context HttpHeaders headers) {
        var vehicle = vehicleRepository.findByCode(code);
        if (vehicle.isEmpty()) {
            return Response.status(Status.NOT_FOUND).build();
        }

        var apiKey = headers.getHeaderString("X-VEHICLE-API-KEY");
        if (apiKey == null || !vehicle.get().hasAuthorisationWithKey(apiKey)) {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        var now = OffsetDateTime.now();

        var measurement = measurementMapper.toMeasurement(vehicle.get(), measurementDto, now);
        measurementRepository.save(measurement);

        return Response.status(Status.CREATED).build();
    }
}
