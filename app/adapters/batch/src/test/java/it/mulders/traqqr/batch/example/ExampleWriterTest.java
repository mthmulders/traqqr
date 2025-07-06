package it.mulders.traqqr.batch.example;

import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.BatchJobItem;
import it.mulders.traqqr.domain.batch.BatchJobItemStatus;
import it.mulders.traqqr.domain.batch.BatchJobStatus;
import it.mulders.traqqr.domain.batch.BatchJobType;
import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.Source;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.mem.batch.InMemoryBatchJobItemRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ExampleWriterTest implements WithAssertions {
    private final InMemoryBatchJobItemRepository repository = new InMemoryBatchJobItemRepository();
    private final ExampleWriter exampleWriter = new ExampleWriter(repository);

    @Test
    void should_store_items_in_repository() {
        var now = OffsetDateTime.now();
        var batchJob = new BatchJob(
                now.minusMinutes(1),
                now,
                now,
                BatchJobType.EXAMPLE,
                BatchJobStatus.COMPLETED,
                Map.of(BatchJobItemStatus.PROCESSED, 1L),
                1L,
                1L);
        var vehicle = new Vehicle("00000", "Example", "nobody", Collections.emptyList(), new BigDecimal(80_000));
        var measurement = new Measurement(
                UUID.randomUUID(),
                now.minusHours(1),
                now.minusHours(1),
                3_000,
                new Measurement.Battery((byte) 85),
                new Measurement.Location(0.0, 0.0),
                Source.API,
                vehicle);

        var item = new BatchJobItem<>(batchJob, BatchJobItemStatus.PROCESSED, measurement);
        exampleWriter.writeItems(List.of(item));

        assertThat(repository.getBatchJobItems()).hasSize(1).contains(item);
    }
}
