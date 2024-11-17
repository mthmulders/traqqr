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

@RequestScoped
@Path("/v1/vehicle/{code}/measurement")
public class MeasurementResource {
    @Inject
    private MeasurementRepository measurementRepository;

    @Inject
    private VehicleRepository vehicleRepository;

    @POST
    public Response registerMeasurement(@PathParam("code") String code, MeasurementDto measurementDto, @Context HttpHeaders headers) {
        String apiKey = headers.getHeaderString("X-VEHICLE-API-KEY");
        Vehicle vehicle = vehicleRepository.findByCode(code)
                .filter(v -> v.hasAuthorisationWithHashedKey(apiKey))
                .orElse(null);

        if (vehicle == null) {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        Measurement measurement = new Measurement(
                UUID.randomUUID(),
                vehicle,
                measurementDto.timestamp(),
                measurementDto.odometer(),
                new Measurement.Battery(measurementDto.battery().soc()),
                new Measurement.Location(measurementDto.location().lat(), measurementDto.location().lon())
        );

        measurementRepository.save(measurement);
        return Response.status(Status.CREATED).build();
    }
}
