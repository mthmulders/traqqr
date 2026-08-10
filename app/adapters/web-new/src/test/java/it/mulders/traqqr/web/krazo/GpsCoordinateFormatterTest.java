package it.mulders.traqqr.web.krazo;

import it.mulders.traqqr.domain.measurements.Measurement;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GpsCoordinateFormatterTest implements WithAssertions {
    private final GpsCoordinateFormatter formatter = new GpsCoordinateFormatter();

    @Test
    void should_format_gps_coordinate() {
        // Arrange
        var location = new Measurement.Location(57.72596, 13.313623 );

        // Act
        var result = formatter.format(location);

        // Assert
        assertThat(result).isEqualTo("57.72596, 13.313623");
    }

    @Test
    void should_accept_null_input() {
        // Act
        var result = formatter.format(null);

        // Assert
        assertThat(result).isEqualTo("");
    }
}