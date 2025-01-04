package it.mulders.traqqr.web.vehicles;

import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import it.mulders.traqqr.web.vehicles.model.AuthorisationDTO;
import it.mulders.traqqr.web.vehicles.model.VehicleDTO;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("regenerateVehicleAuthorisationView")
@ViewScoped
public class RegenerateVehicleAuthorisationView implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(RegenerateVehicleAuthorisationView.class);

    // Components
    private final VehicleMapper vehicleMapper;
    private final VehicleRepository vehicleRepository;

    // Data
    private AuthorisationDTO generatedAuthorisation;
    private VehicleDTO selectedVehicle;

    @Inject
    public RegenerateVehicleAuthorisationView(
            final VehicleMapper vehicleMapper, final VehicleRepository vehicleRepository) {
        this.vehicleMapper = vehicleMapper;
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void regenerateApiKey() {
        log.debug("Regenerating API key for vehicle; code={}", selectedVehicle.getCode());
        this.vehicleRepository
                .findByCode(selectedVehicle.getCode())
                .ifPresentOrElse(
                        (vehicle) -> {
                            var authorisation = vehicle.regenerateKey();
                            this.vehicleRepository.update(vehicle);

                            generatedAuthorisation = vehicleMapper.authorisationToDto(authorisation);

                            updateView();
                        },
                        () -> {
                            log.error(
                                    "Tried to generate new API key for non-existing vehicle; code={}",
                                    selectedVehicle.getCode());
                        });
    }

    protected void updateView() {
        PrimeFaces.current().ajax().update("dialogs:vehicle-details-content");
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
}
