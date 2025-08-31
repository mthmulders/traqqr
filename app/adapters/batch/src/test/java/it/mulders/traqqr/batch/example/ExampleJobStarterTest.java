package it.mulders.traqqr.batch.example;

import it.mulders.traqqr.batch.jakarta.DummyJobOperator;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ExampleJobStarterTest implements WithAssertions {
    private final DummyJobOperator jobOperator = new DummyJobOperator();
    private final ExampleJobStarter job = new ExampleJobStarter(jobOperator, null);

    @Test
    void should_start_job() {
        job.doStart();

        assertThat(this.jobOperator.getRequestedJobStarts()).anyMatch(rjs -> "example".equals(rjs.jobXMLName()));
    }
}
