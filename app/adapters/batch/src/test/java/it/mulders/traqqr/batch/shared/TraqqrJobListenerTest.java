package it.mulders.traqqr.batch.shared;

import it.mulders.traqqr.batch.jakarta.DummyJobContext;
import it.mulders.traqqr.batch.jakarta.DummyJobOperator;
import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.BatchJobType;
import jakarta.batch.runtime.context.JobContext;
import java.util.Properties;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TraqqrJobListenerTest implements WithAssertions {
    private final DummyJobOperator jobOperator = new DummyJobOperator();
    private final JobContext jobContext = new DummyJobContext(1, 1);
    private final BatchJobConverter batchJobConverter = new BatchJobConverter();

    private final TraqqrJobListener listener = new TraqqrJobListener(jobOperator, jobContext, batchJobConverter);

    @Test
    void should_expose_BatchJob_through_job_properties() {
        // Arrange
        jobOperator.start("example", new Properties());

        // Act
        listener.beforeJob();

        // Assert
        assertThat(jobContext.getProperties())
                .hasSize(1)
                .allSatisfy((key, value) -> assertThat(value).isNotNull())
                .hasEntrySatisfying(Constants.BATCH_JOB_PROPERTY, value -> {
                    assertThat(value)
                            .isNotNull()
                            .asInstanceOf(InstanceOfAssertFactories.type(BatchJob.class))
                            .hasFieldOrPropertyWithValue("type", BatchJobType.EXAMPLE);
                });
    }
}
