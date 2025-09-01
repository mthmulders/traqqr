package it.mulders.traqqr.batch.repository;

import it.mulders.traqqr.batch.jakarta.DummyJobOperator;
import it.mulders.traqqr.batch.shared.BatchJobConverter;
import it.mulders.traqqr.domain.batch.BatchJobItemRepository;
import it.mulders.traqqr.domain.batch.BatchJobRepository;
import it.mulders.traqqr.domain.batch.BatchJobType;
import it.mulders.traqqr.domain.shared.Pagination;
import it.mulders.traqqr.mem.batch.InMemoryBatchJobItemRepository;
import jakarta.batch.operations.JobOperator;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class JakartaBatchBatchJobRepositoryTest implements WithAssertions {
    private final JobOperator jobOperator = new DummyJobOperator();
    private final BatchJobItemRepository itemRepository = new InMemoryBatchJobItemRepository();
    private final BatchJobConverter batchJobConverter = new BatchJobConverter();

    private final BatchJobRepository repository =
            new JakartaBatchBatchJobRepository(jobOperator, itemRepository, batchJobConverter);

    @Test
    void should_not_crash_when_no_job_names_known() {
        // Arrange
        var jobNames = new String[] {};

        // Act
        var repo =
                new JakartaBatchBatchJobRepository(new DummyJobOperator(jobNames), itemRepository, batchJobConverter);

        // Assert
        assertThat(repo.count()).isEqualTo(0);
        assertThat(repo.findPaginated(new Pagination(0, 1))).isEmpty();
    }

    @Test
    void should_count_jobs() {
        // Act
        var result = repository.count();

        // Assert
        assertThat(result).isEqualTo(9);
    }

    @Test
    void should_find_single_job_using_pagination() {
        // Arrange

        // Act
        var result = repository.findPaginated(new Pagination(2, 1));

        // Assert
        assertThat(result).singleElement().satisfies(job -> {
            assertThat(job.getType()).isEqualTo(BatchJobType.EXAMPLE);
        });
    }

    @Test
    void should_find_multiple_jobs_using_pagination() {
        // Arrange

        // Act
        var result = repository.findPaginated(new Pagination(0, 10));

        // Assert
        assertThat(result).hasSize(3).allSatisfy(job -> {
            assertThat(job.getType()).isEqualTo(BatchJobType.EXAMPLE);
        });
    }

    @Test
    void should_find_latest_run_per_BatchJobType() {
        // Arrange

        // Act
        var result = repository.findLatestRunsPerBatchJobType();
        var resultPerType = result.stream().collect(groupingBy(BatchJob::getType));

        // Assert
        resultPerType.keySet().forEach(type -> assertThat(resultPerType.get(type))
                .isNotNull()
                .singleElement()
                .satisfies(job -> assertThat(job.getType()).isEqualTo(type)));
        Arrays.stream(BatchJobType.values())
                .forEach(type -> assertThat(resultPerType).containsKey(type));
    }
}
