package it.mulders.traqqr.web.vehicles.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class VehicleDTOTest implements WithAssertions {
    @Test
    void should_honor_equals_contract() {
        EqualsVerifier.forClass(VehicleDTO.class)
                .suppress(Warning.NONFINAL_FIELDS, Warning.BIGDECIMAL_EQUALITY)
                .verify();
    }
}
