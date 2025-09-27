package it.mulders.traqqr.mem.batch;

import it.mulders.traqqr.domain.batch.BatchJobItem;
import it.mulders.traqqr.domain.batch.BatchJobItemStatus;
import it.mulders.traqqr.domain.batch.spi.BatchJobItemRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InMemoryBatchJobItemRepository implements BatchJobItemRepository {
    private final Set<BatchJobItem<?>> batchJobItems = new HashSet<>();

    @Override
    public Map<BatchJobItemStatus, Long> findItemCountsForJobInstanceAndExecution(Long instanceId, Long executionId) {
        return Map.of(
                BatchJobItemStatus.PROCESSED, 1L,
                BatchJobItemStatus.FAILED, 0L,
                BatchJobItemStatus.NO_PROCESSING_NECESSARY, 1L);
    }

    @Override
    public void save(BatchJobItem<?> item) {
        batchJobItems.add(item);
    }

    @Override
    public void saveAll(Collection<BatchJobItem<?>> items) {
        batchJobItems.addAll(items);
    }

    public Set<BatchJobItem<?>> getBatchJobItems() {
        return batchJobItems;
    }
}
