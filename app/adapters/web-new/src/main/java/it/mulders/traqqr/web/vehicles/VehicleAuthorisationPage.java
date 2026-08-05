package it.mulders.traqqr.web.vehicles;

import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.spi.VehicleRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@Path("/secure/vehicles/")
@RequestScoped
public class VehicleAuthorisationPage {
    private static final Logger log = LoggerFactory.getLogger(VehicleAuthorisationPage.class);

    // Components
    private VehicleRepository vehicleRepository;
    private VehicleViewMapper vehicleMapper;

    // Data
    private Models models;
    private Owner owner;

    public VehicleAuthorisationPage() {}

    @Inject
    public VehicleAuthorisationPage(
            Models models, Owner owner, VehicleViewMapper vehicleMapper, VehicleRepository vehicleRepository) {
        this.models = models;
        this.owner = owner;
        this.vehicleMapper = vehicleMapper;
        this.vehicleRepository = vehicleRepository;
    }

    @GET
    @Path("/{code}/authorisation/new")
    @Produces(MediaType.TEXT_HTML)
    @Transactional(Transactional.TxType.REQUIRED)
    public Response create(@PathParam("code") String code) {
        return vehicleRepository
                .findByOwnerAndCode(owner, code)
                .map(vehicle -> {
                    log.debug("Regenerating API key for vehicle; code={}", vehicle.code());
                    var authorisation = vehicle.regenerateKey();
                    this.vehicleRepository.update(vehicle);

                    models.put("vehicle", vehicleMapper.vehicleToDto(vehicle));
                    models.put("authorisation", vehicleMapper.authorisationToDto(authorisation));

                    return Response.ok("vehicles/new_authorisation.jsp").build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}
