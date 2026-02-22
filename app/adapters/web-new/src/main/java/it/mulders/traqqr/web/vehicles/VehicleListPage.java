package it.mulders.traqqr.web.vehicles;

import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.spi.VehicleRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@Path("/secure/vehicles")
@RequestScoped
public class VehicleListPage {
    private static final Logger log = LoggerFactory.getLogger(VehicleListPage.class);

    // Components
    private VehicleRepository vehicleRepository;
    private VehicleViewMapper vehicleMapper;

    // Data
    private Models models;
    private Owner owner;

    public VehicleListPage() {}

    @Inject
    public VehicleListPage(
            Models models, Owner owner, VehicleViewMapper vehicleMapper, VehicleRepository vehicleRepository) {
        this.models = models;
        this.owner = owner;
        this.vehicleMapper = vehicleMapper;
        this.vehicleRepository = vehicleRepository;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response show() {
        log.info("Fetching vehicles; owner_id={}", owner.code());
        var vehicles = vehicleRepository.findByOwner(owner).stream()
                .map(vehicleMapper::vehicleToDto)
                .toList();
        ;
        models.put("vehicles", vehicles);
        return Response.ok("vehicles/list.jsp").build();
    }
}
