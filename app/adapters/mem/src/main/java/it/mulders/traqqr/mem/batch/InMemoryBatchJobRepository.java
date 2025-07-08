package it.mulders.traqqr.mem.batch;

import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.BatchJobRepository;
import it.mulders.traqqr.domain.shared.Pagination;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InMemoryBatchJobRepository implements BatchJobRepository {
    private final List<BatchJob> batchJobs = new ArrayList<>();

    @Override
    public Collection<BatchJob> findPaginated(Pagination pagination) {
        return batchJobs.stream()
                .skip(pagination.offset())
                .limit(pagination.limit())
                .toList();
    }

    @Override
    public long count() {
        return batchJobs.size();
    }

    public void addBatchJob(BatchJob batchJob) {
        this.batchJobs.add(batchJob);
    }
}
