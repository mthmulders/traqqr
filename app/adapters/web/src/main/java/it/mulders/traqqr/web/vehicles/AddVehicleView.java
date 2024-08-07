package it.mulders.traqqr.web.vehicles;

import it.mulders.traqqr.domain.shared.RandomStringUtils;
import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import it.mulders.traqqr.web.vehicles.model.VehicleDTO;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.Locale;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("addVehicleView")
@ViewScoped
public class AddVehicleView implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(AddVehicleView.class);

    private final Owner owner;
    private final VehicleRepository vehicleRepository;
    private final VehicleDTO vehicle;

    @Inject
    public AddVehicleView(final VehicleRepository vehicleRepository, final Owner owner) {
        this.owner = owner;
        var code = RandomStringUtils.generateRandomIdentifier(8).toLowerCase(Locale.getDefault());
        this.vehicle = new VehicleDTO(new Vehicle(code, null, owner.code()));
        this.vehicleRepository = vehicleRepository;
    }

    public VehicleDTO getVehicle() {
        return vehicle;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void save() {
        log.debug("Saving vehicle; vehicle={}", vehicle);
        vehicleRepository.save(new Vehicle(vehicle.getCode(), vehicle.getDescription(), owner.code()));

        var stored = vehicleRepository
                .findByCode(vehicle.getCode())
                .orElseThrow(() -> new IllegalStateException("Failed to find vehicle with code=" + vehicle.getCode()));
        PrimeFaces.current().dialog().closeDynamic(stored);
    }

    public void close() {
        PrimeFaces.current().dialog().closeDynamic(null);
    }
}
