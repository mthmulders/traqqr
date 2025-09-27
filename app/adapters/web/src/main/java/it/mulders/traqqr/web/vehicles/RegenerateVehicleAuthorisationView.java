package it.mulders.traqqr.web.vehicles;

import static jakarta.faces.application.FacesMessage.SEVERITY_WARN;

import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.domain.vehicles.spi.VehicleRepository;
import it.mulders.traqqr.web.vehicles.model.AuthorisationDTO;
import it.mulders.traqqr.web.vehicles.model.VehicleDTO;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DialogFrameworkOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("regenerateVehicleAuthorisationView")
@ViewScoped
public class RegenerateVehicleAuthorisationView implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(RegenerateVehicleAuthorisationView.class);

    // UI
    private UIComponent rawKey;

    // Components
    private final FacesContext facesContext;
    private final PrimeFaces primeFaces;
    private final VehicleMapper vehicleMapper;
    private final VehicleRepository vehicleRepository;

    // Data
    private AuthorisationDTO generatedAuthorisation;
    private VehicleDTO selectedVehicle;

    @Inject
    public RegenerateVehicleAuthorisationView(
            final FacesContext facesContext,
            final PrimeFaces primeFaces,
            final VehicleMapper vehicleMapper,
            final VehicleRepository vehicleRepository) {
        this.facesContext = facesContext;
        this.primeFaces = primeFaces;
        this.vehicleMapper = vehicleMapper;
        this.vehicleRepository = vehicleRepository;

        this.selectedVehicle =
                (VehicleDTO) facesContext.getExternalContext().getFlash().get("selectedVehicle");
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void regenerateApiKey() {
        log.debug("Regenerating API key for vehicle; code={}", selectedVehicle.getCode());
        this.vehicleRepository
                .findByCode(selectedVehicle.getCode())
                .ifPresentOrElse(this::regenerateApiKey, this::logVehicleNotFound);
    }

    private void regenerateApiKey(Vehicle vehicle) {
        var authorisation = vehicle.regenerateKey();
        this.vehicleRepository.update(vehicle);
        generatedAuthorisation = vehicleMapper.authorisationToDto(authorisation);

        var msg = new FacesMessage(
                SEVERITY_WARN, "Please note", "You will see this API key only once. Make sure to write it down!");
        facesContext.addMessage(getRawKeyClientId(), msg);

        var options = DialogFrameworkOptions.builder()
                .resizable(false)
                .modal(true)
                .showEffect("fade")
                .hideEffect("fade")
                .closeOnEscape(true)
                .responsive(true)
                .build();
        primeFaces.dialog().openDynamic("regenerate-authorisation", options, null);
    }

    private void logVehicleNotFound() {
        log.error("Tried to generate new API key for non-existing vehicle; code={}", selectedVehicle.getCode());
    }

    public AuthorisationDTO getGeneratedAuthorisation() {
        return generatedAuthorisation;
    }

    public VehicleDTO getSelectedVehicle() {
        return selectedVehicle;
    }

    public void setSelectedVehicle(final VehicleDTO selectedVehicle) {
        this.selectedVehicle = selectedVehicle;
    }

    public UIComponent getRawKey() {
        return rawKey;
    }

    public void setRawKey(final UIComponent rawKey) {
        this.rawKey = rawKey;
    }

    private String getRawKeyClientId() {
        return rawKey.getClientId(facesContext);
    }
}
