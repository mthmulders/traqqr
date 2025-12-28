package it.mulders.traqqr.domain.measurements;

import it.mulders.traqqr.domain.measurements.Measurement.Battery;
import it.mulders.traqqr.domain.measurements.Measurement.Location;
import it.mulders.traqqr.domain.measurements.api.LookupLocationService;
import it.mulders.traqqr.domain.measurements.api.LookupLocationService.LookupLocationOutcome;
import it.mulders.traqqr.domain.measurements.spi.LocationLookup;
import java.time.OffsetDateTime;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LookupLocationServiceImplTest implements WithAssertions {
    private static final Location NULL_DESCRIPTION_LOCATION = new Location(52.320418, 4.7685652, null);
    private static final Location EMPTY_DESCRIPTION_LOCATION = new Location(52.320418, 4.7685652, "");
    private static final Location FILLED_DESCRIPTION_LOCATION =
            new Location(52.320418, 4.7685652, "Wateringen, Netherlands");
    private static final Location NOT_FOUND_LOCATION = new Location(52.382041, 4.8565276, null);

    private final LocationLookup lookup = new LocationLookup() {
        @Override
        public LocationLookupResult lookup(Location location) {
            if (NULL_DESCRIPTION_LOCATION.equals(location)
                    || EMPTY_DESCRIPTION_LOCATION.equals(location)
                    || FILLED_DESCRIPTION_LOCATION.equals(location)) {
                return new LocationLookupResult.Success(location.withDescription("example description"));
            } else if (NOT_FOUND_LOCATION.equals(location)) {
                return new LocationLookupResult.NotFound();
            } else {
                return new LocationLookupResult.Failure(new RuntimeException("Lookup failed"));
            }
        }
    };
    private final LookupLocationService service = new LookupLocationServiceImpl(lookup);

    @Nested
    class LookupLocation {
        @Test
        void should_not_lookup_if_location_is_null() {
            // Arrange
            var measurement = createMeasurement(OffsetDateTime.now(), null);

            // Act
            var result = service.lookupLocation(measurement);

            // Assert
            assertThat(result).isEqualTo(LookupLocationOutcome.NOT_FOUND);
        }

        @Test
        void should_lookup_if_location_description_is_null() {
            // Arrange
            var measurement = createMeasurement(OffsetDateTime.now(), NULL_DESCRIPTION_LOCATION);

            // Act
            var result = service.lookupLocation(measurement);

            // Assert
            assertThat(result).isInstanceOf(LookupLocationOutcome.Success.class);
        }

        @Test
        void should_lookup_if_location_description_is_empty() {
            // Arrange
            var measurement = createMeasurement(OffsetDateTime.now(), EMPTY_DESCRIPTION_LOCATION);

            // Act
            var result = service.lookupLocation(measurement);

            // Assert
            assertThat(result).isInstanceOf(LookupLocationOutcome.Success.class);
        }

        @Test
        void should_not_lookup_if_description_already_filled() {
            // Arrange
            var measurement = createMeasurement(OffsetDateTime.now(), FILLED_DESCRIPTION_LOCATION);

            // Act
            var result = service.lookupLocation(measurement);

            // Assert
            assertThat(result).isEqualTo(LookupLocationOutcome.NOT_NECESSARY);
        }
    }

    @Nested
    class RefreshLocation {
        @Test
        void should_not_lookup_if_location_is_null() {
            // Arrange
            var measurement = createMeasurement(OffsetDateTime.now(), null);

            // Act
            var result = service.refreshLocation(measurement);

            // Assert
            assertThat(result).isEqualTo(LookupLocationOutcome.NOT_FOUND);
        }

        @Test
        void should_lookup_even_if_description_already_filled() {
            // Arrange
            var measurement = createMeasurement(OffsetDateTime.now(), FILLED_DESCRIPTION_LOCATION);

            // Act
            var result = service.refreshLocation(measurement);

            // Assert
            assertThat(result).isInstanceOf(LookupLocationOutcome.Success.class);
        }
    }

    @Nested
    class ResultMapping {
        @Test
        void should_map_NotFound_result() {
            // Arrange
            var measurement = createMeasurement(OffsetDateTime.now(), NOT_FOUND_LOCATION);

            // Act
            var result = service.refreshLocation(measurement);

            // Assert
            assertThat(result).isEqualTo(LookupLocationOutcome.NOT_FOUND);
        }

        @Test
        void should_map_Failure_result() {
            // Arrange
            var unknownLocation = new Location(0.0, 0.0, null);
            var measurement = createMeasurement(OffsetDateTime.now(), unknownLocation);

            // Act
            var result = service.refreshLocation(measurement);

            // Assert
            assertThat(result).isInstanceOf(LookupLocationOutcome.Failure.class);
        }
    }

    private Measurement createMeasurement(OffsetDateTime measurementTimestamp, Location location) {
        return new Measurement(null, null, measurementTimestamp, 10_000, new Battery((byte) 75), location, null, null);
    }
}
