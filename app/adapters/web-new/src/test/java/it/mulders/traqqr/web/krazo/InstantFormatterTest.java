package it.mulders.traqqr.web.krazo;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.time.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class InstantFormatterTest implements WithAssertions {
    private static final OffsetDateTime INPUT =
            OffsetDateTime.of(LocalDate.of(2025, Month.JANUARY, 7), LocalTime.of(22, 14, 13, 0), ZoneOffset.UTC);

    private final InstantFormatter formatter = new InstantFormatter();

    @Test
    void should_format_measurement_timestamp() {
        // Arrange

        // Act
        var result = formatter.format(INPUT);

        // Assert
        assertThat(result).isEqualTo("07 Jan 2025, 22:14:13 (GMT)");
    }

    @Test
    void should_accept_null_input() {
        // Act
        var result = formatter.format(null);

        // Assert
        assertThat(result).isEqualTo("");
    }

}