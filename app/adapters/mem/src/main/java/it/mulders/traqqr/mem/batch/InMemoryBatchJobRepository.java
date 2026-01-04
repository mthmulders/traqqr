package it.mulders.traqqr.mem.batch;

import static java.util.Comparator.comparing;

import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.BatchJobType;
import it.mulders.traqqr.domain.batch.spi.BatchJobRepository;
import it.mulders.traqqr.domain.shared.Pagination;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryBatchJobRepository implements BatchJobRepository {
    private final List<BatchJob> batchJobs = new ArrayList<>();

    @Override
    public Collection<BatchJob> findPaginated(BatchJobType batchJobType, Pagination pagination) {
        return batchJobs.stream()
                .filter(item -> batchJobType.equals(item.getType()))
                .skip(pagination.offset())
                .limit(pagination.limit())
                .toList();
    }

    @Override
    public long count() {
        return batchJobs.size();
    }

    @Override
    public long count(BatchJobType batchJobType) {
        return batchJobs.stream()
                .filter(item -> batchJobType.equals(item.getType()))
                .count();
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

        return result.stream().sorted(comparing(BatchJob::getLastUpdated)).toList();
    }

    @Override
    public Optional<BatchJob> findById(UUID id) {
        return batchJobs.stream().filter(item -> id.equals(item.getId())).findAny();
    }

    public void addBatchJob(BatchJob batchJob) {
        this.batchJobs.add(batchJob);
    }
}
