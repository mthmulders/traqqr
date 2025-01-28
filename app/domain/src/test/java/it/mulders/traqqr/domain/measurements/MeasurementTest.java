package it.mulders.traqqr.domain.measurements;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MeasurementTest implements WithAssertions {
    @Test
    void should_honor_equals_contract() {
        EqualsVerifier.forClass(Measurement.class).verify();
    }
}
