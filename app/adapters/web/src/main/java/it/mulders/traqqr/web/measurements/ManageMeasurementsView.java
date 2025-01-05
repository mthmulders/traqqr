package it.mulders.traqqr.web.measurements;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.MeasurementRepository;
import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import it.mulders.traqqr.web.vehicles.VehicleMapper;
import it.mulders.traqqr.web.vehicles.model.VehicleDTO;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("manageMeasurementsView")
@ViewScoped
public class ManageMeasurementsView implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(ManageMeasurementsView.class);

    private final Collection<VehicleDTO> vehicles;
    private final MeasurementRepository measurementRepository;
    private final VehicleMapper vehicleMapper;
    private final Owner owner;

    private VehicleDTO selectedVehicle;
    private Collection<Measurement> measurementsForSelectedVehicle = Collections.emptyList();

    @Inject
    public ManageMeasurementsView(
            MeasurementRepository measurementRepository,
            VehicleMapper vehicleMapper,
            VehicleRepository vehicleRepository,
            Owner owner) {
        this.measurementRepository = measurementRepository;
        this.vehicleMapper = vehicleMapper;
        this.owner = owner;

        this.vehicles = vehicleRepository.findByOwner(owner).stream()
                .map(vehicleMapper::vehicleToDto)
                .collect(Collectors.toSet());

        log.info("Found vehicles; owner={}, count={}", owner.code(), this.vehicles.size());
    }

    public Collection<VehicleDTO> getVehicles() {
        return vehicles;
    }

    public VehicleDTO getSelectedVehicle() {
        return selectedVehicle;
    }

    public Collection<Measurement> getMeasurementsForSelectedVehicle() {
        return measurementsForSelectedVehicle;
    }

    public void setSelectedVehicle(VehicleDTO selectedVehicle) {
        log.debug("setSelectedVehicle");
        this.selectedVehicle = selectedVehicle;
    }

    public void onVehicleChange() {
        log.debug("onVehicleChange");
        if (selectedVehicle != null) {
            log.info("Finding measurements; vehicle={}", this.selectedVehicle.getCode());
            this.measurementsForSelectedVehicle =
                    this.measurementRepository.findByVehicle(vehicleMapper.vehicleDtoToVehicle(selectedVehicle, owner));
            log.info(
                    "Found measurements; vehicle={}, count={}",
                    this.selectedVehicle.getCode(),
                    this.measurementsForSelectedVehicle.size());
        } else {
            measurementsForSelectedVehicle = null;
        }

        PrimeFaces.current().ajax().update("form:measurements");
    }
}
