package it.mulders.traqqr.domain.batch;

import it.mulders.traqqr.domain.shared.Pagination;
import java.util.Collection;

public interface BatchJobRepository {
    Collection<BatchJob> findPaginated(Pagination pagination);

    long count();
}
