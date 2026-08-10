package it.mulders.traqqr.web.measurements;

import static org.assertj.core.api.InstanceOfAssertFactories.type;

import it.mulders.traqqr.domain.fakes.MeasurementFaker;
import it.mulders.traqqr.domain.fakes.VehicleFaker;
import it.mulders.traqqr.domain.measurements.spi.MeasurementRepository;
import it.mulders.traqqr.domain.vehicles.spi.VehicleRepository;
import it.mulders.traqqr.mem.measurements.InMemoryMeasurementRepository;
import it.mulders.traqqr.mem.vehicles.InMemoryVehicleRepository;
import it.mulders.traqqr.web.AbstractMvcPageTest;
import it.mulders.traqqr.web.measurements.model.VehicleDTO;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MeasurementListPageTest extends AbstractMvcPageTest {
    private final MeasurementRepository measurementRepository = new InMemoryMeasurementRepository();
    private final VehicleViewMapper vehicleViewMapper = new VehicleViewMapperImpl();
    private final VehicleRepository vehicleRepository = new InMemoryVehicleRepository();

    private final MeasurementListPage page = new MeasurementListPage(
            measurementRepository, models, owner, vehicleRepository, vehicleViewMapper);

    @Test
    void without_vehicle_selected_should_render_list_of_vehicles() {
        // Arrange
        vehicleRepository.save(VehicleFaker.createVehicle(owner));
        vehicleRepository.save(VehicleFaker.createVehicle(owner));
        vehicleRepository.save(VehicleFaker.createVehicle(owner));

        // Act
        var response = page.show();

        // Assert
        assertThat(response).hasStatus(200).hasViewName("measurements/no_vehicle.jsp");
        assertThat(models.get("vehicles")).asInstanceOf(COLLECTION).hasSize(3);
    }

    @Test
    void with_non_existing_vehicle_selected_should_return_status_404() {
        // Arrange

        // Act
        var response = page.show("non-existing-code");

        // Assert
        assertThat(response).hasStatus(404);
    }

    @Test
    void with_vehicle_selected_should_render_list_of_measurements() {
        // Arrange
        var vehicle = VehicleFaker.createVehicle(owner);
        IntStream.range(0, 15)
                .mapToObj(_ -> MeasurementFaker.createMeasurement(vehicle))
                .forEach(measurementRepository::save);
        vehicleRepository.save(vehicle);

        // Act
        var response = page.show(vehicle.code());

        // Assert
        assertThat(response).hasStatus(200).hasViewName("measurements/list.jsp");
        assertThat(models.get("vehicle")).asInstanceOf(type(VehicleDTO.class)).isNotNull();
        assertThat(models.get("measurements")).asInstanceOf(COLLECTION).hasSize(15);
    }
}
