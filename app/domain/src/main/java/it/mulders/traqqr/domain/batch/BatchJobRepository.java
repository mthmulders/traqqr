package it.mulders.traqqr.domain.batch;

import it.mulders.traqqr.domain.shared.Pagination;
import java.util.Collection;
import java.util.List;

public interface BatchJobRepository {
    Collection<BatchJob> findPaginated(Pagination pagination);

    long count();

    List<BatchJob> findLatestRunsPerBatchJobType();
}
