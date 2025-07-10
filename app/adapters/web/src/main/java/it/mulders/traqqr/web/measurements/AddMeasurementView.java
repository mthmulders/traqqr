package it.mulders.traqqr.web.measurements;

import static jakarta.faces.application.FacesMessage.SEVERITY_INFO;

import it.mulders.traqqr.domain.measurements.MeasurementRepository;
import it.mulders.traqqr.domain.measurements.Source;
import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import it.mulders.traqqr.web.measurements.model.MeasurementDTO;
import it.mulders.traqqr.web.vehicles.VehicleMapper;
import it.mulders.traqqr.web.vehicles.model.VehicleDTO;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("addMeasurementView")
@ViewScoped
public class AddMeasurementView implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(AddMeasurementView.class);

    // Components
    private final FacesContext facesContext;
    private final MeasurementMapper measurementMapper;
    private final MeasurementRepository measurementRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    // Parameters
    private String preselectedVehicleCode;

    // Data
    private MeasurementDTO measurementDTO;
    private final Owner owner;
    private VehicleDTO selectedVehicle;

    @Inject
    public AddMeasurementView(
            FacesContext facesContext,
            MeasurementMapper measurementMapper,
            MeasurementRepository measurementRepository,
            VehicleMapper vehicleMapper,
            VehicleRepository vehicleRepository,
            Owner owner) {
        this.facesContext = facesContext;
        this.measurementMapper = measurementMapper;
        this.measurementRepository = measurementRepository;
        this.vehicleMapper = vehicleMapper;
        this.vehicleRepository = vehicleRepository;

        this.owner = owner;

        this.measurementDTO = new MeasurementDTO();
    }

    public void selectVehicle() {
        vehicleRepository
                .findByOwnerAndCode(owner, preselectedVehicleCode)
                .map(vehicleMapper::vehicleToDto)
                .ifPresent(this::setSelectedVehicle);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public String submitMeasurement() {
        log.info("Submitting measurement; vehicle={}", selectedVehicle.getCode());
        log.debug("Submitting measurement; {}", measurementDTO);
        var now = OffsetDateTime.now();
        var measurement = measurementMapper.measurementDtoToMeasurement(measurementDTO, now, now, Source.USER);
        log.debug("Registering measurement; {}", measurement);
        this.measurementRepository.save(measurement);

        var msg = new FacesMessage(SEVERITY_INFO, "Success", "Measurement saved".formatted(selectedVehicle.getCode()));
        facesContext.addMessage(null, msg);

        return "return-measurement-list";
    }

    public String getPreselectedVehicleCode() {
        return preselectedVehicleCode;
    }

    public void setPreselectedVehicleCode(String preselectedVehicleCode) {
        this.preselectedVehicleCode = preselectedVehicleCode;
    }

    public VehicleDTO getSelectedVehicle() {
        return selectedVehicle;
    }

    public void setSelectedVehicle(VehicleDTO selectedVehicle) {
        this.selectedVehicle = selectedVehicle;
        this.measurementDTO.setVehicle(selectedVehicle);
    }

    public MeasurementDTO getMeasurement() {
        return measurementDTO;
    }

    public void setMeasurement(MeasurementDTO measurement) {
        this.measurementDTO = measurement;
    }
}
