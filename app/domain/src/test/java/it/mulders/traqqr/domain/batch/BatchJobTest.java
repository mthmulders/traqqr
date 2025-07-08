package it.mulders.traqqr.domain.batch;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BatchJobTest {
    @Test
    void should_honor_equals_contract() {
        EqualsVerifier.forClass(BatchJob.class).verify();
    }
}
