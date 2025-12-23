package it.mulders.traqqr.batch.locationlookup;

import static it.mulders.traqqr.batch.shared.Constants.BATCH_JOB_PROPERTY;
import static it.mulders.traqqr.domain.fakes.MeasurementFaker.createMeasurement;
import static it.mulders.traqqr.domain.fakes.MeasurementFaker.createMeasurementWithLocation;
import static it.mulders.traqqr.domain.fakes.VehicleFaker.createVehicle;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

import it.mulders.traqqr.batch.jakarta.DummyJobContext;
import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.BatchJobItem;
import it.mulders.traqqr.domain.batch.BatchJobItemStatus;
import it.mulders.traqqr.domain.batch.BatchJobStatus;
import it.mulders.traqqr.domain.batch.BatchJobType;
import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.api.LookupLocationService;
import jakarta.batch.runtime.context.JobContext;
import java.time.OffsetDateTime;
import java.util.EnumMap;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LocationLookupProcessorTest implements WithAssertions {
    private final JobContext jobContext = new DummyJobContext();

    private static final Measurement.Location LOCATION_FOR_FAILURE = new Measurement.Location(1, 2);
    private static final Measurement.Location LOCATION_FOR_NOT_FOUND = new Measurement.Location(2, 3);

    private final LookupLocationService service = new LookupLocationService() {
        @Override
        public LookupLocationOutcome lookupLocation(Measurement measurement) {
            var location = measurement.location();
            if (LOCATION_FOR_FAILURE.equals(location)) {
                return new LookupLocationOutcome.Failure(new RuntimeException("Simulated failure"));
            } else if (LOCATION_FOR_NOT_FOUND.equals(location)) {
                return LookupLocationOutcome.NOT_FOUND;
            } else if (location.description() != null && !location.description().isBlank()) {
                return LookupLocationOutcome.NOT_NECESSARY;
            } else {
                return new LookupLocationOutcome.Success(location.withDescription("Simulated description"));
            }
        }

        @Override
        public LookupLocationOutcome refreshLocation(Measurement measurement) {
            return null;
        }
    };
    private final LocationLookupProcessor processor = new LocationLookupProcessor(jobContext, service);

    @Test
    void measurement_with_location_description_should_return_item_with_status_NO_PROCESSING_NECESSARY()
            throws Exception {
        // Arrange
        var measurement = createMeasurement(createVehicle());
        var location = measurement.location().withDescription("Some Description");
        var measurementWithLocationDescription = measurement.withLocation(location);

        // Act
        var result = processor.processItem(measurementWithLocationDescription);

        // Assert
        assertThat(result)
                .isInstanceOf(BatchJobItem.class)
                .asInstanceOf(type(BatchJobItem.class))
                .extracting(BatchJobItem::status)
                .isEqualTo(BatchJobItemStatus.NO_PROCESSING_NECESSARY);
    }

    @Test
    void measurement_with_unknown_location_should_return_item_with_status_FAILED() throws Exception {
        // Arrange
        var measurement = createMeasurementWithLocation(createVehicle(), LOCATION_FOR_NOT_FOUND);

        // Act
        var result = processor.processItem(measurement);

        // Assert
        assertThat(result)
                .isInstanceOf(BatchJobItem.class)
                .asInstanceOf(type(BatchJobItem.class))
                .extracting(BatchJobItem::status)
                .isEqualTo(BatchJobItemStatus.FAILED);
    }

    @Test
    void location_lookup_failure_should_return_item_with_status_FAILED() throws Exception {
        // Arrange
        var measurement = createMeasurementWithLocation(createVehicle(), LOCATION_FOR_FAILURE);

        // Act
        var result = processor.processItem(measurement);

        // Assert
        assertThat(result)
                .isInstanceOf(BatchJobItem.class)
                .asInstanceOf(type(BatchJobItem.class))
                .extracting(BatchJobItem::status)
                .isEqualTo(BatchJobItemStatus.FAILED);
    }

    @Test
    void location_lookup_should_return_item_with_status_PROCESSED() throws Exception {
        // Arrange
        var measurement = createMeasurementWithLocation(createVehicle(), new Measurement.Location(0, 0));

        // Act
        var result = processor.processItem(measurement);

        // Assert
        assertThat(result)
                .isInstanceOf(BatchJobItem.class)
                .asInstanceOf(type(BatchJobItem.class))
                .extracting(BatchJobItem::status)
                .isEqualTo(BatchJobItemStatus.PROCESSED);
    }

    @Test
    void location_lookup_should_return_item_with_location_description() throws Exception {
        // Arrange
        var measurement = createMeasurementWithLocation(createVehicle(), new Measurement.Location(0, 0));

        // Act
        var result = processor.processItem(measurement);

        // Assert
        assertThat(result)
                .isInstanceOf(BatchJobItem.class)
                .asInstanceOf(type(BatchJobItem.class))
                .extracting(BatchJobItem::item, type(Measurement.class))
                .extracting(Measurement::location)
                .extracting(Measurement.Location::description)
                .isEqualTo("Simulated description");
    }

    @BeforeEach
    void prepareJobContext() {
        var now = OffsetDateTime.now();
        jobContext
                .getProperties()
                .put(
                        BATCH_JOB_PROPERTY,
                        new BatchJob(
                                now.minusMinutes(2),
                                null,
                                now,
                                BatchJobType.LOCATION_LOOKUP,
                                BatchJobStatus.RUNNING,
                                new EnumMap<>(BatchJobItemStatus.class),
                                1L,
                                1L));
    }
}
