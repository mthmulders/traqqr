package it.mulders.traqqr.batch.example;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.MeasurementRepository;
import it.mulders.traqqr.domain.measurements.Source;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.mem.measurements.InMemoryMeasurementRepository;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static java.util.Collections.emptyList;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ExampleReaderTest implements WithAssertions {
    @Test
    void should_return_null_when_no_items() {
        // Arrange
        var repository = new InMemoryMeasurementRepository();
        var reader = new ExampleReader(repository);
        reader.open(null);

        // Act
        var result = reader.readItem();

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void should_return_item_when_available() {
        // Arrange
        var repository = new InMemoryMeasurementRepository();
        var reader = new ExampleReader(repository);

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
        repository.save(item);

        reader.open(null);

        // Act
        var result = reader.readItem();

        // Assert
        assertThat(result).isNotNull().isEqualTo(item);
    }
}