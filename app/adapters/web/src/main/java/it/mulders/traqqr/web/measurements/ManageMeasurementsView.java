package it.mulders.traqqr.web.measurements;

import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import it.mulders.traqqr.web.vehicles.VehicleMapper;
import it.mulders.traqqr.web.vehicles.model.VehicleDTO;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Collectors;

@Named("manageMeasurementsView")
@ViewScoped
public class ManageMeasurementsView implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(ManageMeasurementsView.class);

    private final Collection<VehicleDTO> vehicles;
    private VehicleDTO selectedVehicle;

    @Inject
    public ManageMeasurementsView(VehicleMapper vehicleMapper, VehicleRepository vehicleRepository, Owner owner) {
        this.vehicles = vehicleRepository.findByOwner(owner).stream()
                .map(vehicleMapper::vehicleToDto)
                .collect(Collectors.toSet());
        log.info("Found {} vehicles for owner {}", this.vehicles.size(), owner.code());
    }

    public Collection<VehicleDTO> getVehicles() {
        return vehicles;
    }

    public VehicleDTO getSelectedVehicle() {
        return selectedVehicle;
    }

    public void setSelectedVehicle(VehicleDTO selectedVehicle) {
        this.selectedVehicle = selectedVehicle;
    }

    public void onVehicleChange() {
        log.info("Selected vehicle={}", selectedVehicle);
    }
}
