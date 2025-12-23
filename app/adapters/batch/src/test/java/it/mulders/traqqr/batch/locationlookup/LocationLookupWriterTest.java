package it.mulders.traqqr.batch.locationlookup;

import static it.mulders.traqqr.domain.fakes.MeasurementFaker.createMeasurement;
import static it.mulders.traqqr.domain.fakes.VehicleFaker.createVehicle;

import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.BatchJobItem;
import it.mulders.traqqr.domain.batch.BatchJobItemStatus;
import it.mulders.traqqr.domain.batch.BatchJobStatus;
import it.mulders.traqqr.domain.batch.BatchJobType;
import it.mulders.traqqr.mem.batch.InMemoryBatchJobItemRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LocationLookupWriterTest implements WithAssertions {
    private final InMemoryBatchJobItemRepository repository = new InMemoryBatchJobItemRepository();
    private final LocationLookupWriter exampleWriter = new LocationLookupWriter(repository);

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
        var measurement = createMeasurement(createVehicle());

        var item = new BatchJobItem<>(batchJob, BatchJobItemStatus.PROCESSED, measurement);
        exampleWriter.writeItems(List.of(item));

        assertThat(repository.getBatchJobItems()).hasSize(1).contains(item);
    }
}
