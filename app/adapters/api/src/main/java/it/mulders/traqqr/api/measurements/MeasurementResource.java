package it.mulders.traqqr.api.measurements;

import it.mulders.traqqr.api.measurements.dto.MeasurementDto;
import it.mulders.traqqr.domain.measurements.MeasurementRepository;
import it.mulders.traqqr.domain.vehicles.Authorisation;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@RequestScoped
@Path("/v1/vehicle/{code}/measurement")
@Consumes("application/json")
@Produces("application/json")
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
        var vehicle = vehicleRepository.findByCode(code);
        if (vehicle.isEmpty()) {
            return Response.status(Status.NOT_FOUND).build();
        }

        var apiKey = headers.getHeaderString("X-VEHICLE-API-KEY");
        if (apiKey == null || !vehicle.get().hasAuthorisationWithKey(apiKey)) {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        var measurement = measurementMapper.toMeasurement(vehicle.get(), measurementDto);
        measurementRepository.save(measurement);

        return Response.status(Status.CREATED).build();
    }
}
