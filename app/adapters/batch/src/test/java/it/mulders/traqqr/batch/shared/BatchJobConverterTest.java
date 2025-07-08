package it.mulders.traqqr.batch.shared;

import it.mulders.traqqr.batch.jakarta.DummyJobExecution;
import it.mulders.traqqr.batch.jakarta.DummyJobInstance;
import it.mulders.traqqr.domain.batch.BatchJobItemStatus;
import it.mulders.traqqr.domain.batch.BatchJobStatus;
import it.mulders.traqqr.domain.batch.BatchJobType;
import jakarta.batch.runtime.BatchStatus;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BatchJobConverterTest implements WithAssertions {
    private final BatchJobConverter converter = new BatchJobConverter();

    @Test
    void should_convert_job_instance_and_execution() {
        var end = Instant.now();
        var start = Instant.now().minusSeconds(10);
        var instance = new DummyJobInstance(1, "example");
        var execution = new DummyJobExecution(
                1,
                "example",
                BatchStatus.COMPLETED,
                Date.from(start),
                Date.from(end),
                "DONE",
                Date.from(start),
                Date.from(end),
                new Properties());

        var result = converter.convert(instance, execution);

        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("instanceId", 1L)
                .hasFieldOrPropertyWithValue("executionId", 1L)
                .hasFieldOrPropertyWithValue("status", BatchJobStatus.COMPLETED)
                .hasFieldOrPropertyWithValue("type", BatchJobType.EXAMPLE);
    }

    @Test
    void should_calculate_total_processed_items_count() {
        var end = Instant.now();
        var start = Instant.now().minusSeconds(10);
        var instance = new DummyJobInstance(1, "example");
        var execution = new DummyJobExecution(
                1,
                "example",
                BatchStatus.COMPLETED,
                Date.from(start),
                Date.from(end),
                "DONE",
                Date.from(start),
                Date.from(end),
                new Properties());
        var counts = Map.of(
                BatchJobItemStatus.PROCESSED, 5L,
                BatchJobItemStatus.NO_PROCESSING_NECESSARY, 1L,
                BatchJobItemStatus.FAILED, 1L);

        var result = converter.convert(instance, execution, counts);

        assertThat(result.getItemsProcessed()).isEqualTo(7L);
    }

    @MethodSource("batchJobStatus")
    @ParameterizedTest
    void should_convert_status(BatchStatus input, BatchJobStatus output) {
        assertThat(converter.fromBatchStatus(input)).isEqualTo(output);
    }

    static Stream<Arguments> batchJobStatus() {
        return Stream.of(
                Arguments.arguments(BatchStatus.COMPLETED, BatchJobStatus.COMPLETED),
                Arguments.arguments(BatchStatus.STARTING, BatchJobStatus.RUNNING),
                Arguments.arguments(BatchStatus.STARTED, BatchJobStatus.RUNNING),
                Arguments.arguments(BatchStatus.FAILED, BatchJobStatus.FAILED));
    }
}
