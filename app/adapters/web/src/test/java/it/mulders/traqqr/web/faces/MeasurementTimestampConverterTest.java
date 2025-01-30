package it.mulders.traqqr.web.faces;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.outputlabel.OutputLabel;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MeasurementTimestampConverterTest implements WithAssertions {
    private final FacesContext facesContext = new MockFacesContext();
    private final MeasurementTimestampConverter converter = new MeasurementTimestampConverter();

    private static final OffsetDateTime INPUT =
            OffsetDateTime.of(LocalDate.of(2025, Month.JANUARY, 7), LocalTime.of(22, 14, 13, 0), ZoneOffset.UTC);

    @Nested
    class GetAsString {
        private final UIComponent component = new OutputLabel();

        @Test
        void should_format_measurement_timestamp() {
            assertThat(converter.getAsString(facesContext, component, INPUT)).isEqualTo("07 Jan 2025, 22:14:13");
        }

        @Test
        void should_accept_null_input() {
            assertThat(converter.getAsString(facesContext, component, null)).isEqualTo("");
        }
    }

    @Nested
    class GetAsObject {
        private final UIComponent component = new InputText();

        @Test
        void should_not_accept_null_context() {
            assertThatThrownBy(() -> converter.getAsObject(null, component, "whatever"))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void should_not_accept_null_component() {
            assertThatThrownBy(() -> converter.getAsObject(facesContext, null, "whatever"))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void should_parse_valid_input() {
            var date = LocalDate.of(2025, Month.JANUARY, 1);
            var time = LocalTime.of(10, 31, 4, 0);

            assertThat(converter.getAsObject(facesContext, component, "01 Jan 2025, 10:31:04"))
                    .isEqualTo(OffsetDateTime.of(date, time, ZoneOffset.UTC));
        }

        @Test
        void should_not_parse_invalid_input() {
            assertThatThrownBy(() -> converter.getAsObject(facesContext, component, "01 Foo 2025, 10:31:04"))
                    .isInstanceOf(DateTimeParseException.class);
        }
    }
}
