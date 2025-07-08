package it.mulders.traqqr.batch.example;

import static it.mulders.traqqr.domain.fakes.MeasurementFaker.createMeasurement;
import static it.mulders.traqqr.domain.fakes.VehicleFaker.createVehicle;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ExampleItemProcessListenerTest implements WithAssertions {
    private final ExampleItemProcessListener listener = new ExampleItemProcessListener();

    @Test
    void onProcessError_should_rethrow_Exception() {
        var item = createMeasurement(createVehicle());

        var ex = new Exception();
        assertThatThrownBy(() -> listener.onProcessError(item, ex)).isEqualTo(ex);
    }
}
