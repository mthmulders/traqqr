package it.mulders.traqqr.web.vehicles;

import static jakarta.faces.application.FacesMessage.SEVERITY_INFO;

import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.Vehicle;
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
import org.primefaces.event.RowEditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("vehicleListView")
@ViewScoped
public class VehicleListView implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(VehicleListView.class);

    private final Owner owner;
    private final VehicleRepository vehicleRepository;
    private final Collection<VehicleDTO> vehicles;

    @Inject
    public VehicleListView(final VehicleRepository vehicleRepository, final Owner owner) {
        this.owner = owner;
        this.vehicleRepository = vehicleRepository;

        this.vehicles = vehicleRepository.findByOwnerId(owner).stream()
                .map(VehicleDTO::new) // MapStruct would be better
                .collect(Collectors.toSet());
    }

    public Collection<VehicleDTO> getVehicles() {
        return vehicles;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void onRowEdit(final RowEditEvent<VehicleDTO> event) {
        var vehicle = event.getObject();

        vehicleRepository.update(new Vehicle(vehicle.getCode(), vehicle.getDescription(), owner.code()));

        var msg = new FacesMessage(SEVERITY_INFO, "Success", "Vehicle %s updated".formatted(vehicle.getCode()));
        FacesContext.getCurrentInstance().addMessage(null, msg);

        log.debug("Vehicle edit completed; code={}", vehicle.getCode());
    }
}
