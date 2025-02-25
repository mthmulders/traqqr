package it.mulders.traqqr.web.measurements.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class MeasurementDTOTest implements WithAssertions {
    @Test
    void should_honor_equals_contract() {
        EqualsVerifier.forClass(MeasurementDTO.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}
