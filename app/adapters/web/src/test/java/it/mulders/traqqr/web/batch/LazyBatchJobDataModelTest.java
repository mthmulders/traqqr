package it.mulders.traqqr.web.batch;

import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.BatchJobItemStatus;
import it.mulders.traqqr.domain.batch.BatchJobStatus;
import it.mulders.traqqr.domain.batch.BatchJobType;
import it.mulders.traqqr.mem.batch.InMemoryBatchJobRepository;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.IntStream;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.primefaces.model.LazyDataModel;

class LazyBatchJobDataModelTest implements WithAssertions {
    private final InMemoryBatchJobRepository repository = new InMemoryBatchJobRepository();
    private final LazyDataModel<BatchJob> model = new LazyBatchJobDataModel(repository, BatchJobType.EXAMPLE);

    @Test
    void fetch_single_item() {
        var batchJob = createBatchJob();
        repository.addBatchJob(batchJob);

        var result = model.load(0, 10, null, null);

        assertThat(result).isNotNull().hasSize(1).containsExactly(batchJob);
    }

    @Test
    void count_single_item() {
        var batchJob = createBatchJob();
        repository.addBatchJob(batchJob);

        var result = model.count(null);

        assertThat(result).isEqualTo(1);
    }

    @Test
    void fetch_multiple_items() {
        IntStream.range(0, 50).forEach(idx -> repository.addBatchJob(createBatchJob()));

        var result = model.load(0, 10, null, null);

        assertThat(result).isNotNull().hasSize(10);
    }

    @Test
    void count_multiple_items() {
        IntStream.range(0, 50).forEach(idx -> repository.addBatchJob(createBatchJob()));

        var result = model.count(null);

        assertThat(result).isEqualTo(50);
    }

    private BatchJob createBatchJob() {
        var now = OffsetDateTime.now();
        return new BatchJob(
                now.minusMinutes(1),
                now,
                now,
                BatchJobType.EXAMPLE,
                BatchJobStatus.COMPLETED,
                Map.of(
                        BatchJobItemStatus.PROCESSED, 10L,
                        BatchJobItemStatus.NO_PROCESSING_NECESSARY, 3L,
                        BatchJobItemStatus.FAILED, 2L),
                1L,
                1L);
    }
}
