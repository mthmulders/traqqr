package it.mulders.traqqr.batch.example;

import static java.util.Collections.emptyList;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.Source;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ExampleItemProcessListenerTest implements WithAssertions {
    private final ExampleItemProcessListener listener = new ExampleItemProcessListener();

    @Test
    void onProcessError_should_rethrow_Exception() throws Exception {
        var vehicle = new Vehicle("00000", "vehicle", "owner00", emptyList(), new BigDecimal(85));
        var item = new Measurement(
                UUID.randomUUID(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                13_000,
                new Measurement.Battery((byte) 85),
                new Measurement.Location(0, 0),
                Source.API,
                vehicle);

        var ex = new Exception();
        assertThatThrownBy(() -> listener.onProcessError(item, ex)).isEqualTo(ex);
    }
}
