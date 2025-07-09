package it.mulders.traqqr.web.vehicles;

import static it.mulders.traqqr.domain.fakes.OwnerFaker.createOwner;
import static it.mulders.traqqr.domain.fakes.VehicleFaker.createVehicle;

import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import it.mulders.traqqr.mem.vehicles.InMemoryVehicleRepository;
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

    private final ManageVehicleView view = new ManageVehicleView(mapper, repository, owner);

    @Test
    void should_populate_vehicles_on_creation() {
        // Arrange

        // Act
        var result = view.getVehicles();

        // Assert
        assertThat(result).isNotNull().hasSize(1);
    }
}
