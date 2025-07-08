package it.mulders.traqqr.batch.example;

import static it.mulders.traqqr.domain.fakes.MeasurementFaker.createMeasurement;
import static it.mulders.traqqr.domain.fakes.VehicleFaker.createVehicle;

import it.mulders.traqqr.mem.measurements.InMemoryMeasurementRepository;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

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
        var item = createMeasurement(createVehicle());
        repository.save(item);

        reader.open(null);

        // Act
        var result = reader.readItem();

        // Assert
        assertThat(result).isNotNull().isEqualTo(item);
    }
}
