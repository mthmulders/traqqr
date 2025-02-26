package it.mulders.traqqr.web.measurements;

import it.mulders.traqqr.domain.measurements.MeasurementRepository;
import it.mulders.traqqr.domain.measurements.Source;
import it.mulders.traqqr.domain.shared.RandomStringUtils;
import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import it.mulders.traqqr.mem.measurements.InMemoryMeasurementRepository;
import it.mulders.traqqr.mem.vehicles.InMemoryVehicleRepository;
import it.mulders.traqqr.web.faces.MockFacesContext;
import it.mulders.traqqr.web.measurements.model.MeasurementDTO;
import it.mulders.traqqr.web.user.DummyOwner;
import it.mulders.traqqr.web.vehicles.VehicleMapper;
import it.mulders.traqqr.web.vehicles.VehicleMapperImpl;
import java.math.BigDecimal;
import java.util.Collections;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

class AddMeasurementViewTest implements WithAssertions {
    private static final MockFacesContext facesContext = new MockFacesContext();

    private final MeasurementMapper measurementMapper = new MeasurementMapperImpl();
    private final MeasurementRepository measurementRepository = new InMemoryMeasurementRepository();
    private final Owner owner = DummyOwner.builder()
            .code(RandomStringUtils.generateRandomIdentifier(5))
            .build();
    private final VehicleMapper vehicleMapper = new VehicleMapperImpl();
    private final VehicleRepository vehicleRepository = new InMemoryVehicleRepository();

    private final AddMeasurementView view =
            new AddMeasurementView(measurementMapper, measurementRepository, vehicleMapper, vehicleRepository, owner);

    @AfterAll
    static void cleanupFacesContext() {
        facesContext.unregister();
    }

    @Test
    void should_lookup_preselected_vehicle() {
        // Arrange
        var vehicleCode = "sdfjkshfjkds";
        vehicleRepository.save(new Vehicle(vehicleCode, "", owner.code(), Collections.emptySet(), BigDecimal.TEN));
        view.setPreselectedVehicleCode(vehicleCode);

        // Act
        view.selectVehicle();

        // Assert
        var selectedVehicle = view.getSelectedVehicle();
        assertThat(selectedVehicle).isNotNull();
        assertThat(selectedVehicle.getCode()).isEqualTo(vehicleCode);
    }

    @Test
    void should_store_measurement_with_source() {
        // Arrange
        var vehicleCode = "sdfjkshfjkds";
        var vehicle = new Vehicle(vehicleCode, "", owner.code(), Collections.emptySet(), BigDecimal.TEN);
        vehicleRepository.save(vehicle);

        view.setPreselectedVehicleCode(vehicleCode);
        view.selectVehicle();

        view.setMeasurement(new MeasurementDTO());
        view.getMeasurement().setVehicle(vehicleMapper.vehicleToDto(vehicle));

        // Act
        view.submitMeasurement();

        // Assert
        assertThat(measurementRepository.findByVehicle(vehicle)).anySatisfy(measurement -> {
            assertThat(measurement.source()).isEqualTo(Source.USER);
        });
    }
}
