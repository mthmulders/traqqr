package it.mulders.traqqr.web.vehicles;

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
import java.util.Collection;
import java.util.stream.Collectors;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DialogFrameworkOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("manageVehicleView")
@ViewScoped
public class ManageVehicleView implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(ManageVehicleView.class);

    // Components
    private final VehicleMapper vehicleMapper;
    private final VehicleRepository vehicleRepository;

    // Data
    private final Owner owner;
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
    public void deleteVehicle() {
        this.vehicleRepository.removeVehicle(this.vehicleMapper.vehicleDtoToVehicle(selectedVehicle, owner));

        log.debug("Vehicle removed; code={}", selectedVehicle.getCode());

        var msg = new FacesMessage("Vehicle %s removed".formatted(selectedVehicle.getCode()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
        PrimeFaces.current().ajax().update("form:messages", "form:vehicles");

        this.selectedVehicle = null;
        populateVehicles();
    }

    public void editVehicle(final VehicleDTO vehicle) {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("selectedVehicle", vehicle);
        openDialog("edit");
    }

    public void createVehicle() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("selectedVehicle", new VehicleDTO());
        openDialog("edit");
    }

    public void regenerateApiKey(final VehicleDTO vehicle) {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("selectedVehicle", vehicle);
        openDialog("regenerate-authorisation");
    }

    private void openDialog(final String viewName) {
        var options = DialogFrameworkOptions.builder()
                .resizable(false)
                .modal(true)
                .showEffect("fade")
                .hideEffect("fade")
                .closeOnEscape(true)
                .responsive(true)
                .build();
        PrimeFaces.current().dialog().openDynamic(viewName, options, null);
    }

    public void onVehicleUpdated(SelectEvent<FacesMessage> event) {
        var msg = event.getObject();
        FacesContext.getCurrentInstance().addMessage(null, msg);
        PrimeFaces.current().ajax().update("form:messages");
        populateVehicles();
    }
}
