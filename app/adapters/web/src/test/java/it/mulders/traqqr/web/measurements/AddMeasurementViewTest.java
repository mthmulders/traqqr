package it.mulders.traqqr.web.measurements;

import static it.mulders.traqqr.domain.fakes.VehicleFaker.createVehicle;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.api.RegisterMeasurementService;
import it.mulders.traqqr.domain.shared.RandomStringUtils;
import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import it.mulders.traqqr.mem.vehicles.InMemoryVehicleRepository;
import it.mulders.traqqr.web.faces.FacesContextMock;
import it.mulders.traqqr.web.measurements.model.MeasurementDTO;
import it.mulders.traqqr.web.user.DummyOwner;
import it.mulders.traqqr.web.vehicles.VehicleMapper;
import it.mulders.traqqr.web.vehicles.VehicleMapperImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AddMeasurementViewTest implements WithAssertions {
    private final FacesContextMock facesContext = new FacesContextMock();
    private final MeasurementMapper measurementMapper = new MeasurementMapperImpl();
    private final Owner owner = DummyOwner.builder()
            .code(RandomStringUtils.generateRandomIdentifier(5))
            .build();
    private final Vehicle vehicle = createVehicle(owner);
    private final VehicleMapper vehicleMapper = new VehicleMapperImpl();
    private final VehicleRepository vehicleRepository = new InMemoryVehicleRepository(Collections.singleton(vehicle));
    private final TestRegisterMeasurementService registerMeasurementService = new TestRegisterMeasurementService();

    private final AddMeasurementView view = new AddMeasurementView(
            facesContext, measurementMapper, vehicleMapper, vehicleRepository, registerMeasurementService, owner);

    @Test
    void should_lookup_preselected_vehicle() {
        // Arrange
        view.setPreselectedVehicleCode(vehicle.code());

        // Act
        view.selectVehicle();

        // Assert
        var selectedVehicle = view.getSelectedVehicle();
        assertThat(selectedVehicle).isNotNull();
        assertThat(selectedVehicle.getCode()).isEqualTo(vehicle.code());
    }

    @Test
    void should_store_measurement_with_vehicle() {
        // Arrange
        view.setPreselectedVehicleCode(vehicle.code());
        view.selectVehicle();

        view.setMeasurement(new MeasurementDTO());
        view.getMeasurement().setVehicle(vehicleMapper.vehicleToDto(vehicle));

        // Act
        view.submitMeasurement();

        // Assert
        var measurements = registerMeasurementService.getMeasurements().get(vehicle.code());
        assertThat(measurements).anySatisfy(measurement -> {
            assertThat(measurement.vehicle().code()).isEqualTo(vehicle.code());
        });
    }

    private static class TestRegisterMeasurementService implements RegisterMeasurementService {
        private final Map<String, Collection<Measurement>> measurements = new HashMap<>();

        @Override
        public RegisterMeasurementOutcome registerAutomatedMeasurement(
                String vehicleCode, String apiKey, Measurement measurement) {
            throw new IllegalStateException();
        }

        @Override
        public RegisterMeasurementOutcome registerManualMeasurement(String vehicleCode, Measurement measurement) {
            if ("non-existing-vehicle".equals(vehicleCode)) {
                return RegisterMeasurementOutcome.UNKNOWN_VEHICLE;
            } else {
                measurements.putIfAbsent(vehicleCode, new ArrayList<>());
                measurements.get(vehicleCode).add(measurement);
                return RegisterMeasurementOutcome.SUCCESS;
            }
        }

        public Map<String, Collection<Measurement>> getMeasurements() {
            return measurements;
        }
    }
}
