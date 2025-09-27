package it.mulders.traqqr.web.measurements;

import static java.util.stream.Collectors.toMap;

import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.spi.VehicleRepository;
import it.mulders.traqqr.web.vehicles.VehicleMapper;
import it.mulders.traqqr.web.vehicles.model.VehicleDTO;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Map;
import java.util.function.Function;

@FacesConverter(value = "vehicleConverter", managed = true)
@Named
@ViewScoped // Must be view scoped because the items are tied to the logged-in user.
public class VehicleConverter implements Converter<VehicleDTO>, Serializable {
    private final Map<String, VehicleDTO> vehicles;

    @Inject
    public VehicleConverter(VehicleMapper vehicleMapper, VehicleRepository vehicleRepository, Owner owner) {
        this.vehicles = vehicleRepository.findByOwner(owner).stream()
                .map(vehicleMapper::vehicleToDto)
                .collect(toMap(VehicleDTO::getCode, Function.identity()));
    }

    @Override
    public VehicleDTO getAsObject(FacesContext facesContext, UIComponent uiComponent, String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        return vehicles.get(input);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, VehicleDTO vehicle) {
        return vehicle == null ? null : vehicle.getCode();
    }
}
