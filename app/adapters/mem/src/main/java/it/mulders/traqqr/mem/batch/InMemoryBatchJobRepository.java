package it.mulders.traqqr.mem.batch;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.BatchJobRepository;
import it.mulders.traqqr.domain.batch.BatchJobType;
import it.mulders.traqqr.domain.shared.Pagination;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Override
    public List<BatchJob> findLatestRunsPerBatchJobType() {
        var result = new ArrayList<BatchJob>(BatchJobType.values().length);
        Arrays.stream(BatchJobType.values()).forEach(type -> {
            // Find the last run for this type.
            batchJobs.stream()
                    .filter(job -> type.equals(job.getType()))
                    .sorted(comparing(BatchJob::getLastUpdated))
                    .limit(1)
                    .findAny()
                    .ifPresent(result::add);
        });

        return result.stream().sorted(comparing(BatchJob::getLastUpdated)).collect(toList());
    }

    public void addBatchJob(BatchJob batchJob) {
        this.batchJobs.add(batchJob);
    }
}
