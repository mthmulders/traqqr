package it.mulders.traqqr.batch.locationlookup;

import static it.mulders.traqqr.domain.fakes.MeasurementFaker.createMeasurement;
import static it.mulders.traqqr.domain.fakes.MeasurementFaker.createMeasurementWithLocation;
import static it.mulders.traqqr.domain.fakes.VehicleFaker.createVehicle;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.spi.MeasurementRepository;
import it.mulders.traqqr.mem.measurements.InMemoryMeasurementRepository;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.IntStream;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LocationLookupReaderTest implements WithAssertions {
    private final MeasurementRepository repository = new InMemoryMeasurementRepository();
    private final LocationLookupReader reader = new LocationLookupReader(repository);

    @Test
    void should_retrieve_at_most_100_measurements_without_location_description() throws Exception {
        // Arrange
        var vehicle = createVehicle();
        IntStream.range(0, 200).forEach(i -> repository.save(createMeasurement(vehicle)));
        repository.save(createMeasurementWithLocation(vehicle, new Measurement.Location(55.0, 6.0, "Somewhere")));

        // Act
        reader.open(null);
        var result = collectItems();

        // Assert
        assertThat(result).hasSizeGreaterThan(0).hasSizeLessThanOrEqualTo(100).allSatisfy(measurement -> {
            var location = measurement.location();
            assertThat(location.description()).isNullOrEmpty();
        });
    }

    private Collection<Measurement> collectItems() throws Exception {
        var result = new LinkedList<Measurement>();
        var item = reader.readItem();
        while (item != null) {
            result.add((Measurement) item);
            item = reader.readItem();
        }
        return result;
    }
}
