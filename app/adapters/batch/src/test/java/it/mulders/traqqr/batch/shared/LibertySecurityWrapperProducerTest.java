package it.mulders.traqqr.batch.shared;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LibertySecurityWrapperProducerTest implements WithAssertions {
    @Test
    void should_produce_SecurityWrapper_instance() {
        assertThat(new LibertySecurityWrapperProducer().wrapper()).isNotNull();
    }

}