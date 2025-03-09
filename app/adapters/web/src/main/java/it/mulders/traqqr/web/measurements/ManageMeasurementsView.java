package it.mulders.traqqr.web.measurements;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.MeasurementRepository;
import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import it.mulders.traqqr.web.vehicles.VehicleMapper;
import it.mulders.traqqr.web.vehicles.model.VehicleDTO;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Collectors;
import org.primefaces.PrimeFaces;
import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("manageMeasurementsView")
@ViewScoped
public class ManageMeasurementsView implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(ManageMeasurementsView.class);

    // Components
    private final MeasurementRepository measurementRepository;
    private final VehicleMapper vehicleMapper;

    // Data
    private LazyDataModel<Measurement> measurementsForSelectedVehicle;
    private final Owner owner;
    private Measurement selectedMeasurement;
    private VehicleDTO selectedVehicle;
    private final Collection<VehicleDTO> vehicles;

    // Parameters
    private String preselectedVehicleCode;

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

    public void selectVehicle() {
        if (preselectedVehicleCode != null) {
            vehicles.stream()
                    .filter(vehicle -> vehicle.getCode().equals(preselectedVehicleCode))
                    .findAny()
                    .ifPresent(this::setSelectedVehicle);
            populateMeasurementsForSelectedVehicle();
        }
    }

    public Collection<VehicleDTO> getVehicles() {
        return vehicles;
    }

    public VehicleDTO getSelectedVehicle() {
        return selectedVehicle;
    }

    public LazyDataModel<Measurement> getMeasurementsForSelectedVehicle() {
        return measurementsForSelectedVehicle;
    }

    public void setSelectedVehicle(VehicleDTO selectedVehicle) {
        this.selectedVehicle = selectedVehicle;
    }

    public void populateMeasurementsForSelectedVehicle() {
        if (selectedVehicle != null) {
            log.info("Finding measurements; vehicle={}", this.selectedVehicle.getCode());
            var selectedVehicle = vehicleMapper.vehicleDtoToVehicle(this.selectedVehicle, owner);
            this.measurementsForSelectedVehicle = new LazyMeasurementDataModel(measurementRepository, selectedVehicle);
        } else {
            measurementsForSelectedVehicle = null;
        }

        PrimeFaces.current().ajax().update("form:measurements");
    }

    public void setSelectedMeasurement(Measurement measurement) {
        this.selectedMeasurement = measurement;
    }

    public String getPreselectedVehicleCode() {
        return preselectedVehicleCode;
    }

    public void setPreselectedVehicleCode(String preselectedVehicleCode) {
        this.preselectedVehicleCode = preselectedVehicleCode;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteSelectedMeasurement() {
        log.info(
                "Removing measurement; vehicle={}, id={}",
                selectedMeasurement.vehicle().code(),
                selectedMeasurement.id());

        measurementRepository.removeMeasurement(selectedMeasurement);

        var msg = new FacesMessage("1 measurement removed");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        PrimeFaces.current().ajax().update("form:messages", "form:measurements");

        this.selectedMeasurement = null;
        populateMeasurementsForSelectedVehicle();
    }
}
