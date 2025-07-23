package it.mulders.traqqr.web.measurements;

import static it.mulders.traqqr.domain.fakes.VehicleFaker.createVehicle;

import it.mulders.traqqr.domain.measurements.MeasurementRepository;
import it.mulders.traqqr.domain.measurements.Source;
import it.mulders.traqqr.domain.shared.RandomStringUtils;
import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import it.mulders.traqqr.mem.measurements.InMemoryMeasurementRepository;
import it.mulders.traqqr.mem.vehicles.InMemoryVehicleRepository;
import it.mulders.traqqr.web.faces.FacesContextMock;
import it.mulders.traqqr.web.measurements.model.MeasurementDTO;
import it.mulders.traqqr.web.user.DummyOwner;
import it.mulders.traqqr.web.vehicles.VehicleMapper;
import it.mulders.traqqr.web.vehicles.VehicleMapperImpl;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AddMeasurementViewTest implements WithAssertions {
    private final FacesContextMock facesContext = new FacesContextMock();
    private final MeasurementMapper measurementMapper = new MeasurementMapperImpl();
    private final MeasurementRepository measurementRepository = new InMemoryMeasurementRepository();
    private final Owner owner = DummyOwner.builder()
            .code(RandomStringUtils.generateRandomIdentifier(5))
            .build();
    private final VehicleMapper vehicleMapper = new VehicleMapperImpl();
    private final VehicleRepository vehicleRepository = new InMemoryVehicleRepository();

    private final AddMeasurementView view = new AddMeasurementView(
            facesContext, measurementMapper, measurementRepository, vehicleMapper, vehicleRepository, owner);

    @Test
    void should_lookup_preselected_vehicle() {
        // Arrange
        var vehicle = createVehicle(owner);
        vehicleRepository.save(vehicle);
        view.setPreselectedVehicleCode(vehicle.code());

        // Act
        view.selectVehicle();

        // Assert
        var selectedVehicle = view.getSelectedVehicle();
        assertThat(selectedVehicle).isNotNull();
        assertThat(selectedVehicle.getCode()).isEqualTo(vehicle.code());
    }

    @Test
    void should_store_measurement_with_source() {
        // Arrange
        var vehicle = createVehicle(owner);
        vehicleRepository.save(vehicle);

        view.setPreselectedVehicleCode(vehicle.code());
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
