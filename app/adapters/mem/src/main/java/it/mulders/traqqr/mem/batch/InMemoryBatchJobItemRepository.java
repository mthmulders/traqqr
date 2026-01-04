package it.mulders.traqqr.mem.batch;

import it.mulders.traqqr.domain.batch.BatchJobItem;
import it.mulders.traqqr.domain.batch.BatchJobItemStatus;
import it.mulders.traqqr.domain.batch.spi.BatchJobItemRepository;
import it.mulders.traqqr.domain.shared.Identifiable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class InMemoryBatchJobItemRepository implements BatchJobItemRepository {
    private final Map<UUID, BatchJobItem<Identifiable>> batchJobItems = new HashMap<>();

    @Override
    public Map<BatchJobItemStatus, Long> findItemCountsForJobInstanceAndExecution(Long instanceId, Long executionId) {
        return Map.of(
                BatchJobItemStatus.PROCESSED, 1L,
                BatchJobItemStatus.FAILED, 0L,
                BatchJobItemStatus.NO_PROCESSING_NECESSARY, 1L);
    }

    @Override
    public void save(BatchJobItem<Identifiable> item) {
        batchJobItems.put(UUID.randomUUID(), item);
    }

    @Override
    public void saveAll(Collection<BatchJobItem<Identifiable>> items) {
        items.forEach(this::save);
    }

    public Set<BatchJobItem<?>> getBatchJobItems() {
        return new HashSet<>(batchJobItems.values());
    }

    @Override
    public Optional<BatchJobItem<Identifiable>> findById(UUID id) {
        return batchJobItems.entrySet().stream()
                .filter(entry -> id.equals(entry.getKey()))
                .map(Map.Entry::getValue)
                .findAny();
    }
}
