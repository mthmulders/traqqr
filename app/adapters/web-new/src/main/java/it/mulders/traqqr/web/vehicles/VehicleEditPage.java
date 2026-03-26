package it.mulders.traqqr.web.vehicles;

import it.mulders.traqqr.domain.shared.RandomStringUtils;
import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.spi.VehicleRepository;
import it.mulders.traqqr.web.vehicles.model.VehicleDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.security.CsrfProtected;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@Path("/secure/vehicles/")
@RequestScoped
public class VehicleEditPage {
    private static final Logger log = LoggerFactory.getLogger(VehicleEditPage.class);

    // Components
    private VehicleRepository vehicleRepository;
    private VehicleViewMapper vehicleMapper;

    // Data
    private Models models;
    private Owner owner;

    public VehicleEditPage() {}

    @Inject
    public VehicleEditPage(
            Models models, Owner owner, VehicleViewMapper vehicleMapper, VehicleRepository vehicleRepository) {
        this.models = models;
        this.owner = owner;
        this.vehicleMapper = vehicleMapper;
        this.vehicleRepository = vehicleRepository;
    }

    @GET
    @Path("/{code}")
    @Produces(MediaType.TEXT_HTML)
    public Response edit(@PathParam("code") String code) {
        return vehicleRepository
                .findByOwnerAndCode(owner, code)
                .map(vehicleMapper::vehicleToDto)
                .map(vehicle -> {
                    models.put("vehicle", vehicle);
                    models.put("title", "Edit vehicle");
                    return Response.ok("vehicles/edit.jsp").build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/new")
    @Produces(MediaType.TEXT_HTML)
    public Response create() {
        models.put("title", "Create vehicle");
        models.put("vehicle", new VehicleDTO());
        return Response.ok("vehicles/edit.jsp").build();
    }

    @CsrfProtected
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @POST
    @Path("/{code}")
    @Produces(MediaType.TEXT_HTML)
    public Response save(@PathParam("code") String code, @BeanParam VehicleDTO vehicleDTO) {
        vehicleRepository.findByOwnerAndCode(owner, code).ifPresent(existing -> {
            var updated = vehicleMapper.dtoToVehicle(vehicleDTO, owner);
            vehicleRepository.update(existing.updateWith(updated));
        });
        return Response.seeOther(URI.create("/secure/vehicles")).build();
    }

    @CsrfProtected
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @POST
    @Path("/new")
    @Produces(MediaType.TEXT_HTML)
    public Response save(@BeanParam VehicleDTO vehicleDTO) {
        vehicleDTO.setCode(RandomStringUtils.generateRandomIdentifier(8));
        var vehicle = vehicleMapper.dtoToVehicle(vehicleDTO, owner);
        vehicleRepository.save(vehicle);
        return Response.seeOther(URI.create("/secure/vehicles")).build();
    }
}
