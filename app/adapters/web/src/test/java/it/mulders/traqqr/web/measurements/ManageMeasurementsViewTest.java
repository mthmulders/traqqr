package it.mulders.traqqr.web.measurements;

import static it.mulders.traqqr.domain.fakes.OwnerFaker.createOwner;
import static it.mulders.traqqr.domain.fakes.VehicleFaker.createVehicle;

import it.mulders.traqqr.domain.measurements.MeasurementRepository;
import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import it.mulders.traqqr.mem.measurements.InMemoryMeasurementRepository;
import it.mulders.traqqr.mem.vehicles.InMemoryVehicleRepository;
import it.mulders.traqqr.web.faces.FacesContextMock;
import it.mulders.traqqr.web.vehicles.VehicleMapper;
import it.mulders.traqqr.web.vehicles.VehicleMapperImpl;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ManageMeasurementsViewTest implements WithAssertions {
    private final Owner owner = createOwner();
    private final Vehicle vehicle = createVehicle(owner);

    private final FacesContextMock facesContext = new FacesContextMock();
    private final MeasurementRepository measurementRepository = new InMemoryMeasurementRepository();
    private final VehicleMapper vehicleMapper = new VehicleMapperImpl();
    private final VehicleRepository vehicleRepository = new InMemoryVehicleRepository() {
        {
            this.save(vehicle);
        }
    };

    private final ManageMeasurementsView view =
            new ManageMeasurementsView(facesContext, measurementRepository, vehicleMapper, vehicleRepository, owner);

    @Test
    void should_populate_vehicles() {
        // Arrange
        var expectedVehicle = vehicleMapper.vehicleToDto(vehicle);

        // Act

        // Assert
        assertThat(view.getVehicles()).contains(expectedVehicle);
    }
}
