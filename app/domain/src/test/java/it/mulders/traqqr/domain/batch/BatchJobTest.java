package it.mulders.traqqr.domain.batch;

import java.util.Map;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BatchJobTest implements WithAssertions {
    @Test
    void should_honor_equals_contract() {
        EqualsVerifier.forClass(BatchJob.class).verify();
    }

    @Test
    void should_calculate_number_successful_items() {
        // Arrange
        var job = new BatchJob(
                null,
                null,
                null,
                null,
                null,
                Map.of(
                        BatchJobItemStatus.PROCESSED, 3L,
                        BatchJobItemStatus.FAILED, 2L,
                        BatchJobItemStatus.NO_PROCESSING_NECESSARY, 4L),
                null,
                null);

        // Act
        var itemsProcessedSuccess = job.getItemsProcessedSuccess();

        // Assert
        assertThat(itemsProcessedSuccess).isEqualTo(3L);
    }

    @Test
    void should_calculate_number_failed_items() {
        // Arrange
        var job = new BatchJob(
                null,
                null,
                null,
                null,
                null,
                Map.of(
                        BatchJobItemStatus.PROCESSED, 3L,
                        BatchJobItemStatus.FAILED, 2L,
                        BatchJobItemStatus.NO_PROCESSING_NECESSARY, 4L),
                null,
                null);

        // Act
        var itemsProcessingFailed = job.getItemsProcessingFailed();

        // Assert
        assertThat(itemsProcessingFailed).isEqualTo(2L);
    }

    @Test
    void should_calculate_number_unprocessed_items() {
        // Arrange
        var job = new BatchJob(
                null,
                null,
                null,
                null,
                null,
                Map.of(
                        BatchJobItemStatus.PROCESSED, 3L,
                        BatchJobItemStatus.FAILED, 2L,
                        BatchJobItemStatus.NO_PROCESSING_NECESSARY, 4L),
                null,
                null);

        // Act
        var itemsProcessingNotNecessary = job.getItemsProcessingNotNecessary();

        // Assert
        assertThat(itemsProcessingNotNecessary).isEqualTo(4L);
    }

    @Test
    void should_calculate_total_number_items() {
        // Arrange
        var job = new BatchJob(
                null,
                null,
                null,
                null,
                null,
                Map.of(
                        BatchJobItemStatus.PROCESSED, 3L,
                        BatchJobItemStatus.FAILED, 2L,
                        BatchJobItemStatus.NO_PROCESSING_NECESSARY, 4L),
                null,
                null);

        // Act
        var result = job.getTotalItemsProcessed();

        // Assert
        assertThat(result).isEqualTo(9L);
    }
}
