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
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.MatchMode;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LazyBatchJobDataModelTest implements WithAssertions {
    private final InMemoryBatchJobRepository repository = new InMemoryBatchJobRepository();
    private final LazyDataModel<BatchJob> model = new LazyBatchJobDataModel(repository);

    private final FilterMeta filterOnTypeExample = FilterMeta.builder()
            .field("type")
            .matchMode(MatchMode.EXACT)
            .filterValue(BatchJobType.EXAMPLE.name())
            .build();

    @Test
    void fetch_single_item() {
        var batchJob = createBatchJob();
        repository.addBatchJob(batchJob);

        var result = model.load(0, 10, null, Map.of("type", filterOnTypeExample));

        assertThat(result).isNotNull().hasSize(1).containsExactly(batchJob);
    }

    @Test
    void count_single_item() {
        var batchJob = createBatchJob();
        repository.addBatchJob(batchJob);

        var result = model.count(Map.of("type", filterOnTypeExample));

        assertThat(result).isEqualTo(1);
    }

    @Test
    void fetch_multiple_items() {
        IntStream.range(0, 50).forEach(idx -> repository.addBatchJob(createBatchJob()));

        var result = model.load(0, 10, null, Map.of("type", filterOnTypeExample));

        assertThat(result).isNotNull().hasSize(10);
    }

    @Test
    void count_multiple_items() {
        IntStream.range(0, 50).forEach(idx -> repository.addBatchJob(createBatchJob()));

        var result = model.count(Map.of("type", filterOnTypeExample));

        assertThat(result).isEqualTo(50);
    }

    @Test
    void count_should_return_0_when_no_filter() {
        IntStream.range(0, 50).forEach(idx -> repository.addBatchJob(createBatchJob()));

        var result = model.count(Map.of());

        assertThat(result).isEqualTo(0);
    }

    @Test
    void load_should_return_nothing_when_no_filter() {
        IntStream.range(0, 50).forEach(idx -> repository.addBatchJob(createBatchJob()));

        var result = model.load(0, 10, null, Map.of());

        assertThat(result).isNotNull().hasSize(0);
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
