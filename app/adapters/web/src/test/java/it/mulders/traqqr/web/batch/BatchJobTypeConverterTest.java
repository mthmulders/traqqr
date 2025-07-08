package it.mulders.traqqr.web.batch;

import it.mulders.traqqr.domain.batch.BatchJobType;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BatchJobTypeConverterTest implements WithAssertions {
    private final BatchJobTypeConverter converter = new BatchJobTypeConverter();

    @Test
    void should_not_break_on_null_input() {
        assertThat(converter.getAsObject(null, null, null)).isNull();
        assertThat(converter.getAsString(null, null, null)).isNull();
    }

    @Test
    void should_not_break_on_empty_input() {
        assertThat(converter.getAsObject(null, null, "")).isNull();
    }

    @Test
    void should_use_name_as_display_value() {
        assertThat(converter.getAsString(null, null, BatchJobType.EXAMPLE)).isEqualTo("EXAMPLE");
    }

    @Test
    void should_lookup_type_by_name() {
        assertThat(converter.getAsObject(null, null, "EXAMPLE")).isEqualTo(BatchJobType.EXAMPLE);
    }

    @Test
    void should_break_on_non_existing_name() {
        assertThatThrownBy(() -> converter.getAsObject(null, null, "DOES_NOT_EXIST"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
