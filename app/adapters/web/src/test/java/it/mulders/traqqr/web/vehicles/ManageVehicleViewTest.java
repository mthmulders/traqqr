package it.mulders.traqqr.web.vehicles;

import static it.mulders.traqqr.domain.fakes.OwnerFaker.createOwner;
import static it.mulders.traqqr.domain.fakes.VehicleFaker.createVehicle;

import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.domain.vehicles.spi.VehicleRepository;
import it.mulders.traqqr.mem.vehicles.InMemoryVehicleRepository;
import it.mulders.traqqr.web.faces.FacesContextMock;
import it.mulders.traqqr.web.prime.PrimeFacesMock;
import it.mulders.traqqr.web.vehicles.model.VehicleDTO;
import jakarta.faces.context.FacesContext;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ManageVehicleViewTest implements WithAssertions {
    private final VehicleMapper mapper = new VehicleMapperImpl();
    private final Owner owner = createOwner();
    private final Vehicle vehicle = createVehicle(owner);
    private final VehicleRepository repository = new InMemoryVehicleRepository() {
        {
            this.save(vehicle);
        }
    };
    private final FacesContext facesContext = new FacesContextMock();
    private final PrimeFacesMock primeFacesMock = new PrimeFacesMock();

    private final ManageVehicleView view =
            new ManageVehicleView(facesContext, primeFacesMock, mapper, repository, owner);

    @Test
    void should_populate_vehicles_on_creation() {
        // Arrange

        // Act
        var result = view.getVehicles();

        // Assert
        assertThat(result).isNotNull().hasSize(1);
    }

    @Test
    void delete_should_remove_vehicle() {
        // Arrange
        view.setSelectedVehicle(mapper.vehicleToDto(vehicle));

        // Act
        view.deleteVehicle();

        // Assert
        assertThat(repository.findByOwner(owner)).isEmpty();
    }

    @Test
    void should_open_dialog_for_new_vehicle() {
        // Arrange
        primeFacesMock.dialog().reset();

        // Act
        view.createVehicle();

        // Assert
        assertThat(primeFacesMock.dialog().openedDialogs())
                .hasSize(1)
                .singleElement()
                .isEqualTo("edit");
    }

    @Test
    void should_create_new_vehicle_for_editing() {
        // Arrange
        primeFacesMock.dialog().reset();

        // Act
        view.createVehicle();

        // Assert
        assertThat(facesContext.getExternalContext().getFlash().size()).isGreaterThan(0);
        assertThat(facesContext.getExternalContext().getFlash()).anySatisfy((key, value) -> {
            assertThat(value).isInstanceOf(VehicleDTO.class);
        });
    }

    @Test
    void should_open_dialog_for_editing_existing_vehicle() {
        // Arrange
        primeFacesMock.dialog().reset();
        var vehicle = createVehicle();

        // Act
        view.editVehicle(mapper.vehicleToDto(vehicle));

        // Assert
        assertThat(primeFacesMock.dialog().openedDialogs())
                .hasSize(1)
                .singleElement()
                .isEqualTo("edit");
    }

    @Test
    void should_select_vehicle_for_editing() {
        // Arrange
        primeFacesMock.dialog().reset();
        var vehicle = createVehicle();
        var dto = mapper.vehicleToDto(vehicle);

        // Act
        view.editVehicle(dto);

        // Assert
        assertThat(facesContext.getExternalContext().getFlash().containsValue(dto))
                .isTrue();
    }
}
