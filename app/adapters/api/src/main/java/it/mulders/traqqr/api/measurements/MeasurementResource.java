package it.mulders.traqqr.api.measurements;

import it.mulders.traqqr.api.measurements.dto.MeasurementDto;
import it.mulders.traqqr.domain.measurements.api.RegisterMeasurementService;
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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@ApplicationScoped
@Path("/v1/vehicle/{code}/measurement")
public class MeasurementResource {
    private MeasurementMapper measurementMapper;
    private RegisterMeasurementService registerMeasurementService;

    public MeasurementResource() {
        // Only here to satisfy CDI spec.
    }

    @Inject
    public MeasurementResource(
            MeasurementMapper measurementMapper, RegisterMeasurementService registerMeasurementService) {
        this.measurementMapper = measurementMapper;
        this.registerMeasurementService = registerMeasurementService;
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Transactional
    public Response registerMeasurement(
            @PathParam("code") String code, MeasurementDto measurementDto, @Context HttpHeaders headers) {
        var apiKey = headers.getHeaderString("X-VEHICLE-API-KEY");

        var measurement = measurementMapper.toMeasurement(measurementDto);

        var result = registerMeasurementService.registerAutomatedMeasurement(code, apiKey, measurement);

        var httpStatus =
                switch (result) {
                    case SUCCESS -> Status.CREATED;
                    case UNAUTHORIZED -> Status.UNAUTHORIZED;
                    case UNKNOWN_VEHICLE -> Status.NOT_FOUND;
                };

        return Response.status(httpStatus).build();
    }
}
