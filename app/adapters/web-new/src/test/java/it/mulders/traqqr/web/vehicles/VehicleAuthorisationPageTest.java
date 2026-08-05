package it.mulders.traqqr.web.vehicles;

import static org.assertj.core.api.InstanceOfAssertFactories.type;

import it.mulders.traqqr.domain.fakes.VehicleFaker;
import it.mulders.traqqr.domain.vehicles.spi.VehicleRepository;
import it.mulders.traqqr.mem.vehicles.InMemoryVehicleRepository;
import it.mulders.traqqr.web.AbstractMvcPageTest;
import it.mulders.traqqr.web.vehicles.model.AuthorisationDTO;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class VehicleAuthorisationPageTest extends AbstractMvcPageTest {
    private final VehicleViewMapper vehicleViewMapper = new VehicleViewMapperImpl();
    private final VehicleRepository vehicleRepository = new InMemoryVehicleRepository();
    private final VehicleAuthorisationPage page =
            new VehicleAuthorisationPage(models, owner, vehicleViewMapper, vehicleRepository);

    @Test
    void should_return_status_404_for_non_existing_vehicle() {
        // Arrange

        // Act
        var response = page.create("non-existing-code");

        // Assert
        assertThat(response).hasStatus(404);
    }

    @Test
    void should_return_edit_view_for_create() {
        // Arrange
        var vehicle = VehicleFaker.createVehicle(owner);
        vehicleRepository.save(vehicle);

        // Act
        var response = page.create(vehicle.code());

        // Assert
        assertThat(response).hasStatus(200).hasViewName("vehicles/new_authorisation.jsp");
    }

    @Test
    void should_generate_and_store_new_authorisation_for_existing_vehicle() {
        // Arrange
        var vehicle = VehicleFaker.createVehicle(owner);
        vehicleRepository.save(vehicle);

        // Act
        page.create(vehicle.code());

        // Assert
        assertThat(vehicleRepository.findByCode(vehicle.code()))
                .isPresent()
                .hasValueSatisfying(
                        (updated) -> assertThat(updated.authorisations()).hasSize(1));
    }

    @Test
    void should_have_new_authorisation_in_models() {
        // Arrange
        var vehicle = VehicleFaker.createVehicle(owner);
        vehicleRepository.save(vehicle);

        // Act
        page.create(vehicle.code());

        // Assert
        assertThat(models.get("authorisation"))
                .asInstanceOf(type(AuthorisationDTO.class))
                .satisfies(
                        authorisation -> assertThat(authorisation.getRawKey()).isNotEmpty());
    }
}
