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
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("maintainVehicleView")
@ViewScoped
public class MaintainVehicleView implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(MaintainVehicleView.class);

    // Components
    private final VehicleMapper vehicleMapper;
    private final VehicleRepository vehicleRepository;

    // Data
    private final Owner owner;
    private VehicleDTO selectedVehicle;
    ;

    @Inject
    public MaintainVehicleView(VehicleMapper vehicleMapper, VehicleRepository vehicleRepository, Owner owner) {
        this.owner = owner;
        this.vehicleMapper = vehicleMapper;
        this.vehicleRepository = vehicleRepository;

        this.selectedVehicle = (VehicleDTO) FacesContext.getCurrentInstance()
                .getExternalContext()
                .getFlash()
                .get("selectedVehicle");
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void saveVehicle() {
        if (selectedVehicle.getCode() == null) {
            selectedVehicle.setCode(RandomStringUtils.generateRandomIdentifier(8));
            this.vehicleRepository.save(vehicleMapper.vehicleDtoToVehicle(selectedVehicle, owner));

            var msg =
                    new FacesMessage(SEVERITY_INFO, "Success", "Vehicle %s saved".formatted(selectedVehicle.getCode()));
            PrimeFaces.current().dialog().closeDynamic(msg);
        } else {
            vehicleRepository.update(this.vehicleMapper.vehicleDtoToVehicle(selectedVehicle, owner));

            var msg = new FacesMessage(
                    SEVERITY_INFO, "Success", "Vehicle %s updated".formatted(selectedVehicle.getCode()));
            PrimeFaces.current().dialog().closeDynamic(msg);
        }
    }

    public VehicleDTO getSelectedVehicle() {
        return selectedVehicle;
    }

    public void setSelectedVehicle(VehicleDTO selectedVehicle) {
        this.selectedVehicle = selectedVehicle;
    }
}
