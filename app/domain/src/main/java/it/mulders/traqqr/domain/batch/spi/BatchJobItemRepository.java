package it.mulders.traqqr.domain.batch.spi;

import it.mulders.traqqr.domain.batch.BatchJobItem;
import it.mulders.traqqr.domain.batch.BatchJobItemStatus;
import it.mulders.traqqr.domain.shared.Identifiable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface BatchJobItemRepository {
    Map<BatchJobItemStatus, Long> findItemCountsForJobInstanceAndExecution(Long instanceId, Long executionId);

    void save(BatchJobItem<Identifiable> item);

    void saveAll(Collection<BatchJobItem<Identifiable>> items);

    Optional<BatchJobItem<Identifiable>> findById(UUID id);
}
