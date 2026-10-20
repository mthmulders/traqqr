package it.mulders.traqqr.web.measurements;

import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.spi.VehicleRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@Path("/secure/measurements/new")
@RequestScoped
public class MeasurementEditPage {
    private static final Logger log = LoggerFactory.getLogger(MeasurementEditPage.class);

    // Components
    private VehicleRepository vehicleRepository;
    private VehicleViewMapper vehicleViewMapper;

    // Data
    private Models models;
    private Owner owner;

    public MeasurementEditPage() {
    }

    @Inject
    public MeasurementEditPage(Models models, Owner owner, VehicleRepository vehicleRepository, VehicleViewMapper vehicleViewMapper) {
        this.models = models;
        this.owner = owner;
        this.vehicleRepository = vehicleRepository;
        this.vehicleViewMapper = vehicleViewMapper;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response create(@QueryParam("vehicle") String vehicleCode) {
        return vehicleRepository.findByOwnerAndCode(owner, vehicleCode)
                .map(vehicleViewMapper::vehicleToDto)
                .map(vehicle -> {
                    models.put("title", "Register measurement");
                    models.put("vehicle", vehicle);
                    models.put("measurement", new Object());

                    return Response.ok("measurements/edit.jsp").build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}
