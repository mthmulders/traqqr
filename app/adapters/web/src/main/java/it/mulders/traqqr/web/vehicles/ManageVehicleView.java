package it.mulders.traqqr.web.vehicles;

import static jakarta.faces.application.FacesMessage.SEVERITY_INFO;

import it.mulders.traqqr.domain.shared.RandomStringUtils;
import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import it.mulders.traqqr.web.vehicles.model.VehicleDTO;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.stream.Collectors;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("manageVehicleView")
@ViewScoped
public class ManageVehicleView implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(ManageVehicleView.class);

    // Components
    private final Owner owner;
    private final VehicleMapper vehicleMapper;
    private final VehicleRepository vehicleRepository;

    // Data
    private VehicleDTO selectedVehicle;
    private Collection<VehicleDTO> vehicles;

    @Inject
    public ManageVehicleView(VehicleMapper vehicleMapper, VehicleRepository vehicleRepository, Owner owner) {
        this.owner = owner;
        this.vehicleMapper = vehicleMapper;
        this.vehicleRepository = vehicleRepository;

        populateVehicles();
    }

    private void populateVehicles() {
        log.info("Fetching vehicles; owner_id={}", owner.code());
        this.vehicles = vehicleRepository.findByOwner(owner).stream()
                .map(this.vehicleMapper::vehicleToDto)
                .collect(Collectors.toSet());
    }

    public Collection<VehicleDTO> getVehicles() {
        return vehicles;
    }

    public VehicleDTO getSelectedVehicle() {
        return selectedVehicle;
    }

    public void setSelectedVehicle(final VehicleDTO selectedVehicle) {
        this.selectedVehicle = selectedVehicle;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void saveVehicle() {
        if (selectedVehicle.getCode() == null) {
            selectedVehicle.setCode(RandomStringUtils.generateRandomIdentifier(8));
            this.vehicleRepository.save(vehicleMapper.vehicleDtoToVehicle(selectedVehicle, owner));

            var msg =
                    new FacesMessage(SEVERITY_INFO, "Success", "Vehicle %s saved".formatted(selectedVehicle.getCode()));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            vehicleRepository.update(this.vehicleMapper.vehicleDtoToVehicle(selectedVehicle, owner));

            var msg = new FacesMessage(
                    SEVERITY_INFO, "Success", "Vehicle %s updated".formatted(selectedVehicle.getCode()));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

        selectedVehicle = null;
        populateVehicles();
        PrimeFaces.current().executeScript("PF('manageVehicleDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:vehicles");
    }

    public void createVehicle() {
        selectedVehicle = new VehicleDTO();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteVehicle() {
        this.vehicleRepository.removeVehicle(this.vehicleMapper.vehicleDtoToVehicle(selectedVehicle, owner));

        log.debug("Vehicle removed; code={}", selectedVehicle.getCode());

        var msg = new FacesMessage("Vehicle %s removed".formatted(selectedVehicle.getCode()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
        PrimeFaces.current().ajax().update("form:messages", "form:vehicles");

        this.selectedVehicle = null;
        populateVehicles();
    }
}
