package it.mulders.traqqr.web.vehicles;

import static org.assertj.core.api.InstanceOfAssertFactories.type;

import it.mulders.traqqr.domain.fakes.VehicleFaker;
import it.mulders.traqqr.domain.vehicles.spi.VehicleRepository;
import it.mulders.traqqr.mem.vehicles.InMemoryVehicleRepository;
import it.mulders.traqqr.web.AbstractMvcPageTest;
import it.mulders.traqqr.web.vehicles.model.VehicleDTO;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class VehicleEditPageTest extends AbstractMvcPageTest {
    private final VehicleViewMapper vehicleViewMapper = new VehicleViewMapperImpl();
    private final VehicleRepository vehicleRepository = new InMemoryVehicleRepository();
    private final VehicleEditPage page = new VehicleEditPage(models, owner, vehicleViewMapper, vehicleRepository);

    @Test
    void should_return_edit_view_for_create() {
        // Act
        var response = page.create();

        // Assert
        assertThat(response).hasStatus(200).hasViewName("vehicles/edit.jsp");
    }

    @Test
    void should_have_correct_title_for_create() {
        // Act
        page.create();

        // Assert
        assertThat(models.get("title")).asInstanceOf(STRING).isEqualTo("Create vehicle");
    }

    @Test
    void should_return_edit_view_for_edit() {
        // Arrange
        var vehicle = VehicleFaker.createVehicle(owner);
        vehicleRepository.save(vehicle);

        // Act
        var response = page.edit(vehicle.code());

        // Assert
        assertThat(response).hasStatus(200).hasViewName("vehicles/edit.jsp");
    }

    @Test
    void should_have_correct_title_for_edit() {
        // Arrange
        var vehicle = VehicleFaker.createVehicle(owner);
        vehicleRepository.save(vehicle);

        // Act
        var response = page.edit(vehicle.code());

        // Assert
        assertThat(models.get("title")).asInstanceOf(STRING).isEqualTo("Edit vehicle");
    }

    @Test
    void should_load_vehicle_for_edit() {
        // Arrange
        var vehicle = VehicleFaker.createVehicle(owner);
        vehicleRepository.save(vehicle);

        // Act
        page.edit(vehicle.code());

        // Assert
        var dto = vehicleViewMapper.vehicleToDto(vehicle);
        assertThat(models.get("vehicle")).asInstanceOf(type(VehicleDTO.class)).isEqualTo(dto);
    }

    @Test
    void should_return_status_404_for_non_existing_vehicle() {
        // Arrange

        // Act
        var response = page.edit("non-existing-code");

        // Assert
        assertThat(response).hasStatus(404);
    }

    @Test
    void should_assign_code_to_new_vehicle() throws Throwable {
        // Arrange
        var description = UUID.randomUUID().toString();
        var dto = new VehicleDTO(null, description, BigDecimal.ONE);

        // Act
        page.save(dto);

        // Assert
        var saved = vehicleRepository.findByOwner(owner).stream()
                .filter(v -> v.description().equals(description))
                .findFirst()
                .orElseThrow(() -> fail("Vehicle not saved"));
        assertThat(saved.code()).isNotNull();
    }

    @Test
    void should_show_list_after_creating_new_vehicle() {
        // Arrange
        var dto = new VehicleDTO("code", "description", BigDecimal.ONE);

        // Act
        var response = page.save(dto);

        // Assert
        assertThat(response).hasStatus(303).hasHeader("location", "/secure/vehicles");
    }

    @Test
    void should_show_list_after_updating_vehicle() {
        // Arrange
        var dto = new VehicleDTO("code", "description", BigDecimal.ONE);

        // Act
        var response = page.save("code", dto);

        // Assert
        assertThat(response).hasStatus(303).hasHeader("location", "/secure/vehicles");
    }

    @Test
    void update_vehicle_should_store_updated_record() throws Throwable {
        // Arrange
        var vehicle = VehicleFaker.createVehicle(owner);
        vehicleRepository.save(vehicle);
        var dto = vehicleViewMapper.vehicleToDto(vehicle);
        dto.setDescription("Updated description");

        // Act
        page.save(vehicle.code(), dto);

        // Assert
        var updated = vehicleRepository.findByOwnerAndCode(owner, vehicle.code())
                .orElseThrow(() -> fail("Existing vehicle not found anymore"));
        assertThat(updated.description()).isEqualTo(dto.getDescription());
    }
}
