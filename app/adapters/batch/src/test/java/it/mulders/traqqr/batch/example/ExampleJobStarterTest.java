package it.mulders.traqqr.batch.example;

import it.mulders.traqqr.batch.jakarta.DummyJobOperator;
import it.mulders.traqqr.domain.batch.BatchJobType;
import it.mulders.traqqr.domain.batch.JobStartRequestedEvent;
import it.mulders.traqqr.libertysecurity.SecurityWrapper;
import java.security.PrivilegedAction;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ExampleJobStarterTest implements WithAssertions {
    private final DummyJobOperator jobOperator = new DummyJobOperator();
    private final SecurityWrapper wrapper = new SecurityWrapper() {
        @Override
        public <T> T execute(PrivilegedAction<T> action) {
            return action.run();
        }
    };
    private final ExampleJobStarter job = new ExampleJobStarter(jobOperator, wrapper);

    @Test
    void should_start_job_manually() {
        // Arrange
        var event = new JobStartRequestedEvent(BatchJobType.EXAMPLE);

        // Act
        job.startManual(event);

        // Assert
        assertThat(this.jobOperator.getRequestedJobStarts()).anyMatch(rjs -> "example".equals(rjs.jobXMLName()));
    }

    @Test
    void should_start_job_automatically() {
        // Arrange

        // Act
        job.startAutomatic();

        // Assert
        assertThat(this.jobOperator.getRequestedJobStarts()).anyMatch(rjs -> "example".equals(rjs.jobXMLName()));
    }
}
