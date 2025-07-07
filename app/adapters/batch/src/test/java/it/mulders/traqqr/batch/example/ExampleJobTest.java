package it.mulders.traqqr.batch.example;

import it.mulders.traqqr.batch.jakarta.DummyJobOperator;
import it.mulders.traqqr.domain.batch.BatchJobType;
import it.mulders.traqqr.domain.batch.JobStartRequestedEvent;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ExampleJobTest implements WithAssertions {
    private final DummyJobOperator jobOperator = new DummyJobOperator();
    private final ExampleJob job = new ExampleJob(jobOperator);

    @Test
    void upon_JobStartRequestedEvent_with_correct_type_should_start_job() {
        var event = new JobStartRequestedEvent(BatchJobType.EXAMPLE);

        job.startManual(event);

        assertThat(this.jobOperator.getRequestedJobStarts()).anyMatch(rjs -> "example".equals(rjs.jobXMLName()));
    }

    @Test
    void should_ignore_JobStartRequestedEvent_with_different_type() {
        var event = new JobStartRequestedEvent(null);

        job.startManual(event);

        assertThat(this.jobOperator.getRequestedJobStarts()).noneMatch(rjs -> rjs.jobXMLName() == null);
    }
}
