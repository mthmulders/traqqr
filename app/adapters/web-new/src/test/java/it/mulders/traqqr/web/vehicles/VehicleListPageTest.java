package it.mulders.traqqr.web.vehicles;

import it.mulders.traqqr.domain.fakes.VehicleFaker;
import it.mulders.traqqr.domain.shared.RandomStringUtils;
import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.spi.VehicleRepository;
import it.mulders.traqqr.mem.vehicles.InMemoryVehicleRepository;
import it.mulders.traqqr.web.AbstractMvcPageTest;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class VehicleListPageTest extends AbstractMvcPageTest {
    private final VehicleViewMapper vehicleViewMapper = new VehicleViewMapperImpl();
    private final VehicleRepository vehicleRepository = new InMemoryVehicleRepository();

    private final VehicleListPage page = new VehicleListPage(models, owner, vehicleViewMapper, vehicleRepository);

    @Test
    void should_return_list_view() {
        // Act
        var response = page.show();

        // Assert
        assertThat(response).hasStatus(200).hasViewName("vehicles/list.jsp");
    }

    @Test
    void should_load_owners_vehicles() {
        // Arrange
        var vehicle = VehicleFaker.createVehicle(owner);
        vehicleRepository.save(vehicle);

        // Act
        page.show();

        // Assert
        assertThat(models.get("vehicles"))
                .asInstanceOf(COLLECTION)
                .containsExactly(vehicleViewMapper.vehicleToDto(vehicle));
    }

    private Owner createOwner() {
        return new Owner() {
            private final String code = RandomStringUtils.generateRandomAlphaString(18);

            @Override
            public String code() {
                return code;
            }

            @Override
            public String displayName() {
                return "";
            }

            @Override
            public String profilePictureUrl() {
                return "";
            }
        };
    }
}
