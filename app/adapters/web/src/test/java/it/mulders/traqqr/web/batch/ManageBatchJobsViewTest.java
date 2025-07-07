package it.mulders.traqqr.web.batch;

import it.mulders.traqqr.domain.batch.BatchJobType;
import it.mulders.traqqr.domain.batch.JobStartRequestedEvent;
import it.mulders.traqqr.mem.batch.InMemoryBatchJobRepository;
import it.mulders.traqqr.web.event.DummyEvent;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class ManageBatchJobsViewTest implements WithAssertions {
    private final InMemoryBatchJobRepository repository = new InMemoryBatchJobRepository();
    private final DummyEvent<JobStartRequestedEvent> event = new DummyEvent<>();
    private final ManageBatchJobsView view = new ManageBatchJobsView(repository, event);

    @Test
    void should_start_batch_job() {
        // Arrange
        view.setSelectedBatchJobType(BatchJobType.EXAMPLE);

        // Act
        view.startBatchJob();

        // Assert
        assertThat(event.getFiredEvents()).singleElement().satisfies(firedEvent -> {
            assertThat(firedEvent.jobType()).isEqualTo(BatchJobType.EXAMPLE);
        });
    }
}
