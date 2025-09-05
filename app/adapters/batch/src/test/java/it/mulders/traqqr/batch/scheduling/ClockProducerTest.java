package it.mulders.traqqr.batch.scheduling;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ClockProducerTest implements WithAssertions {
    @Test
    void should_produce_Clock_instance() {
        assertThat(new ClockProducer().clock()).isNotNull();
    }
}
