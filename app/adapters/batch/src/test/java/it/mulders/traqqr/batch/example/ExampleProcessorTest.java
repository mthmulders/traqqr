package it.mulders.traqqr.batch.example;

import static it.mulders.traqqr.batch.shared.Constants.BATCH_JOB_PROPERTY;

import it.mulders.traqqr.batch.jakarta.DummyJobContext;
import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.BatchJobItem;
import it.mulders.traqqr.domain.batch.BatchJobItemStatus;
import it.mulders.traqqr.domain.batch.BatchJobStatus;
import it.mulders.traqqr.domain.batch.BatchJobType;
import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.Source;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import jakarta.batch.runtime.context.JobContext;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.UUID;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ExampleProcessorTest implements WithAssertions {
    private final JobContext jobContext = new DummyJobContext();
    private final ExampleProcessor processor = new ExampleProcessor(jobContext);

    @BeforeEach
    void prepareJobContext() {
        var now = OffsetDateTime.now();
        jobContext
                .getProperties()
                .put(
                        BATCH_JOB_PROPERTY,
                        new BatchJob(
                                now.minusMinutes(2),
                                null,
                                now,
                                BatchJobType.EXAMPLE,
                                BatchJobStatus.RUNNING,
                                new EnumMap<>(BatchJobItemStatus.class),
                                1L,
                                1L));
    }

    @Test
    void should_create_BatchJobItem_with_Measurement() {
        var now = OffsetDateTime.now();
        var vehicle = new Vehicle("0000", "Example", "0000", new ArrayList<>(), BigDecimal.ONE);
        var measurement = new Measurement(
                UUID.randomUUID(),
                now.minusHours(1),
                now.minusHours(1),
                3_000,
                new Measurement.Battery((byte) 85),
                new Measurement.Location(0.0, 0.0),
                Source.API,
                vehicle);

        var result = processor.processItem(measurement);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(BatchJobItem.class)
                .asInstanceOf(InstanceOfAssertFactories.type(BatchJobItem.class))
                .satisfies(batchJobItem -> {
                    assertThat(batchJobItem.status()).isEqualTo(BatchJobItemStatus.PROCESSED);
                    assertThat(batchJobItem.item())
                            .isInstanceOf(Measurement.class)
                            .isEqualTo(measurement);
                });
    }
}
