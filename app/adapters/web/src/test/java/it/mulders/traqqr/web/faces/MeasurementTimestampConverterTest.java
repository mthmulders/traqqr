package it.mulders.traqqr.web.faces;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MeasurementTimestampConverterTest implements WithAssertions {
    private final MeasurementTimestampConverter converter = new MeasurementTimestampConverter();

    private static final OffsetDateTime INPUT =
            OffsetDateTime.of(LocalDate.of(2025, Month.JANUARY, 7), LocalTime.of(22, 14, 13, 0), ZoneOffset.UTC);

    @Test
    void should_format_measurement_timestamp() {
        assertThat(converter.getAsString(null, null, INPUT)).isEqualTo("07 Jan 2025, 22:14.13");
    }

    @Test
    void should_accept_null_input() {
        assertThat(converter.getAsString(null, null, null)).isEqualTo("");
    }
}
