package it.mulders.traqqr.web.batch;

import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.BatchJobItemStatus;
import it.mulders.traqqr.domain.batch.BatchJobStatus;
import it.mulders.traqqr.domain.batch.BatchJobType;
import it.mulders.traqqr.mem.batch.InMemoryBatchJobRepository;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Map;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.primefaces.model.LazyDataModel;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LazyLatestBatchJobRunsDataModelTest implements WithAssertions {
    private final InMemoryBatchJobRepository repository = new InMemoryBatchJobRepository();
    private final LazyDataModel<BatchJob> model = new LazyLatestBatchJobRunsDataModel(repository);

    @Test
    void count_should_equal_number_of_BatchJobTypes() {
        assertThat(model.count(null)).isEqualTo(BatchJobType.values().length);
    }

    @Test
    void load_should_return_one_item_for_each_BatchJobType() {
        // Arrange
        Arrays.stream(BatchJobType.values()).forEach(type -> {
            // Create multiple so we can be sure we'll only find the latest one
            repository.addBatchJob(createBatchJob(type, 1));
            repository.addBatchJob(createBatchJob(type, 2));
            repository.addBatchJob(createBatchJob(type, 3));
        });

        // Act
        var result = model.load(0, 0, null, null);

        // Assert
        assertThat(result).hasSize(BatchJobType.values().length);
    }

    private BatchJob createBatchJob(BatchJobType type, int minuteTimeOffset) {
        var now = OffsetDateTime.now();
        return new BatchJob(
                now.minusMinutes(minuteTimeOffset),
                now,
                now,
                type,
                BatchJobStatus.COMPLETED,
                Map.of(
                        BatchJobItemStatus.PROCESSED, 10L,
                        BatchJobItemStatus.NO_PROCESSING_NECESSARY, 3L,
                        BatchJobItemStatus.FAILED, 2L),
                1L,
                1L);
    }
}
