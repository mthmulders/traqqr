package it.mulders.traqqr.domain.batch;

import java.util.Collection;
import java.util.Map;

public interface BatchJobItemRepository {
    Map<BatchJobItemStatus, Long> findItemCountsForJobInstanceAndExecution(Long instanceId, Long executionId);

    void save(BatchJobItem<?> item);

    @SuppressWarnings("rawtypes")
    void saveAll(Collection<BatchJobItem<?>> items);
}
