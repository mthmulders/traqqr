package it.mulders.traqqr.mem.batch;

import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.BatchJobRepository;
import it.mulders.traqqr.domain.shared.Pagination;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InMemoryBatchJobRepository implements BatchJobRepository {
    private final Set<BatchJob> batchJobs = new HashSet<>();

    @Override
    public Collection<BatchJob> findPaginated(Pagination pagination) {
        return List.of();
    }

    @Override
    public long count() {
        return batchJobs.size();
    }
}
