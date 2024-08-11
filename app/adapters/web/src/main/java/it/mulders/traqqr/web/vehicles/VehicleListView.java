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
import java.util.Collection;
import java.util.stream.Collectors;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("vehicleListView")
@ViewScoped
public class VehicleListView implements Serializable {
    // TODO Rename to ManageVehicleView?
    private static final Logger log = LoggerFactory.getLogger(VehicleListView.class);

    // Components
    private final Owner owner;
    private final VehicleMapper vehicleMapper;
    private final VehicleRepository vehicleRepository;

    // Data
    private Collection<VehicleDTO> vehicles;
    private VehicleDTO selectedVehicle;

    @Inject
    public VehicleListView(
            final VehicleMapper vehicleMapper, final VehicleRepository vehicleRepository, final Owner owner) {
        this.owner = owner;
        this.vehicleMapper = vehicleMapper;
        this.vehicleRepository = vehicleRepository;

        populateVehicles();
    }

    private void populateVehicles() {
        this.vehicles = vehicleRepository.findByOwnerId(owner).stream()
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
        if (selectedVehicle != null && selectedVehicle.getCode() == null) {
            var code = RandomStringUtils.generateRandomIdentifier(8);
            selectedVehicle.setCode(code);
            this.vehicleRepository.save(vehicleMapper.vehicleDtoToVehicle(selectedVehicle, owner));

            log.debug("Vehicle saved; code={}", code);

            var msg = new FacesMessage(SEVERITY_INFO, "Success", "Vehicle %s saved".formatted(code));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            vehicleRepository.update(this.vehicleMapper.vehicleDtoToVehicle(selectedVehicle, owner));

            log.debug("Vehicle updated; code={}", selectedVehicle.getCode());

            var msg = new FacesMessage(SEVERITY_INFO, "Success", "Vehicle %s updated".formatted(selectedVehicle.getCode()));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

        PrimeFaces.current().executeScript("PF('manageVehicleDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:vehicles");
    }

    public void createVehicle() {
        selectedVehicle = new VehicleDTO();
    }


}
