package it.mulders.traqqr.web.vehicles;

import it.mulders.traqqr.domain.fakes.VehicleFaker;
import it.mulders.traqqr.domain.shared.RandomStringUtils;
import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.spi.VehicleRepository;
import it.mulders.traqqr.mem.vehicles.InMemoryVehicleRepository;
import jakarta.mvc.Models;
import jakarta.ws.rs.core.Response;
import org.assertj.core.api.WithAssertions;
import org.eclipse.krazo.core.ModelsImpl;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class VehicleListPageTest implements WithAssertions {
    private final Models models = new ModelsImpl();
    private final Owner owner = createOwner();
    private final VehicleViewMapper vehicleViewMapper = new VehicleViewMapperImpl();
    private final VehicleRepository vehicleRepository = new InMemoryVehicleRepository();

    private final VehicleListPage page = new VehicleListPage(models, owner, vehicleViewMapper, vehicleRepository);

    @Test
    void should_return_list_view() {
        // Act
        var response = page.show();

        // Assert
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(response.readEntity(String.class)).isEqualTo("vehicles/list.jsp");
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
