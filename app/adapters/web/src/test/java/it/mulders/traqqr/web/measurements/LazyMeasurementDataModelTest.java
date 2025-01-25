package it.mulders.traqqr.web.measurements;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.MeasurementRepository;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.mem.measurements.InMemoryMeasurementRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.IntStream;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.primefaces.model.LazyDataModel;

class LazyMeasurementDataModelTest implements WithAssertions {
    private final MeasurementRepository repository = new InMemoryMeasurementRepository();
    private final Vehicle selectedVehicle =
            new Vehicle("code123", "Code 123", "owner123", Collections.emptyList(), BigDecimal.valueOf(82));

    private final LazyDataModel<Measurement> measurementDataModel =
            new LazyMeasurementDataModel(repository, selectedVehicle);

    @Test
    void fetch_single_item() {
        var measurement = generateMeasurement();
        repository.save(measurement);

        var result = measurementDataModel.load(0, 10, null, null);

        assertThat(result).isNotNull().hasSize(1).containsExactly(measurement);
    }

    @Test
    void count_single_item() {
        repository.save(generateMeasurement());

        var result = measurementDataModel.count(null);

        assertThat(result).isEqualTo(1);
    }

    @Test
    void fetch_multiple_items() {
        IntStream.range(0, 50).forEach(idx -> repository.save(generateMeasurement()));

        var result = measurementDataModel.load(0, 10, null, null);

        assertThat(result).isNotNull().hasSize(10);
    }

    @Test
    void count_multiple_items() {
        IntStream.range(0, 50).forEach(idx -> repository.save(generateMeasurement()));

        var result = measurementDataModel.count(null);

        assertThat(result).isEqualTo(50);
    }

    private Measurement generateMeasurement() {
        return new Measurement(
                UUID.randomUUID(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                10_000,
                new Measurement.Battery((byte) 80),
                new Measurement.Location(52, 6),
                selectedVehicle);
    }
}
