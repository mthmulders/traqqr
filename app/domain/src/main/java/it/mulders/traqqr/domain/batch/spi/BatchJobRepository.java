package it.mulders.traqqr.domain.batch.spi;

import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.BatchJobType;
import it.mulders.traqqr.domain.shared.Pagination;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BatchJobRepository {
    Collection<BatchJob> findPaginated(BatchJobType batchJobType, Pagination pagination);

    long count();

    long count(BatchJobType batchJobType);

    List<BatchJob> findLatestRunsPerBatchJobType();

    Optional<BatchJob> findById(UUID id);
}
