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
    private final LocationLookup lookup = new LocationLookup() {
        @Override
        public LocationLookupResult lookup(Location location) {
            return new LocationLookupResult.Success(location);
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
        void should_not_lookup_if_location_description_is_null() {
            // Arrange
            var location = new Location(52.320418, 4.7685652, null);
            var measurement = createMeasurement(OffsetDateTime.now(), location);

            // Act
            var result = service.lookupLocation(measurement);

            // Assert
            assertThat(result).isInstanceOf(LookupLocationOutcome.Success.class);
        }

        @Test
        void should_not_lookup_if_description_already_filled() {
            // Arrange
            var location = new Location(52.320418, 4.7685652, "Wateringen, Netherlands");
            var measurement = createMeasurement(OffsetDateTime.now(), location);

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
            var location = new Location(52.320418, 4.7685652, "Wateringen, Netherlands");
            var measurement = createMeasurement(OffsetDateTime.now(), location);

            // Act
            var result = service.refreshLocation(measurement);

            // Assert
            assertThat(result).isInstanceOf(LookupLocationOutcome.Success.class);
        }
    }

    private Measurement createMeasurement(OffsetDateTime measurementTimestamp, Location location) {
        return new Measurement(null, null, measurementTimestamp, 10_000, new Battery((byte) 75), location, null, null);
    }
}
