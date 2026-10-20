package it.mulders.traqqr.web.measurements;

import it.mulders.traqqr.domain.measurements.spi.MeasurementRepository;
import it.mulders.traqqr.domain.shared.Pagination;
import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.domain.vehicles.spi.VehicleRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@Path("/secure/measurements")
@RequestScoped
public class MeasurementListPage {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final Logger log = LoggerFactory.getLogger(MeasurementListPage.class);

    // Components
    private MeasurementRepository measurementRepository;
    private VehicleRepository vehicleRepository;
    private VehicleViewMapper vehicleMapper;

    // Data
    private Models models;
    private Owner owner;

    public MeasurementListPage() {}

    @Inject
    public MeasurementListPage(
            MeasurementRepository measurementRepository,
            Models models,
            Owner owner,
            VehicleRepository vehicleRepository,
            VehicleViewMapper vehicleViewMapper) {
        this.measurementRepository = measurementRepository;
        this.models = models;
        this.owner = owner;
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleViewMapper;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response show(@QueryParam("vehicle") String code, @QueryParam("page") Integer page) {
        if (code == null) {
            return showVehicleSelection();
        } else {
            return showMeasurementsForVehicle(code, page);
        }
    }

    private Response showVehicleSelection() {
        log.info("Fetching vehicles; owner_id={}", owner.code());
        var vehicles = vehicleRepository.findByOwner(owner).stream()
                .map(vehicleMapper::vehicleToDto)
                .toList();

        models.put("vehicles", vehicles);
        return Response.ok("measurements/no_vehicle.jsp").build();
    }

    private Response showMeasurementsForVehicle(@QueryParam("vehicle") String code, @QueryParam("page") Integer page) {
        var pageNumber = page == null ? 0 : page;
        log.info("Fetching measurements; owner_id={}, vehicle_code={}, page_number={}", owner.code(), code, pageNumber);
        return vehicleRepository
                .findByOwnerAndCode(owner, code).map(vehicle -> {
                    var pagination = new Pagination(pageNumber * DEFAULT_PAGE_SIZE, DEFAULT_PAGE_SIZE);

                    var measurements = measurementRepository.findByVehicle(vehicle, pagination);

                    models.put("vehicle", vehicleMapper.vehicleToDto(vehicle));
                    models.put("measurements", measurements);
                    models.put("pageNumber", pageNumber);
                    models.put("maximumPageNumber", calculateMaximumPageNumber(vehicle));
                    models.put("showFirstPage", pageNumber != 0 && pageNumber != 1);

                    return Response.ok("measurements/list.jsp").build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    private long calculateMaximumPageNumber(Vehicle vehicle) {
        var measurementCount = measurementRepository.countByVehicle(vehicle);
        return Math.ceilDiv(measurementCount, (long) DEFAULT_PAGE_SIZE) - 1;
    }
}
