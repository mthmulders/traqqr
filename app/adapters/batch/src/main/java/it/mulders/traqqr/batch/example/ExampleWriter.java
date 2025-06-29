package it.mulders.traqqr.batch.example;

import it.mulders.traqqr.domain.batch.BatchJobItem;
import it.mulders.traqqr.domain.batch.BatchJobItemRepository;
import jakarta.batch.api.chunk.AbstractItemWriter;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import java.util.List;

@Dependent
@Named("exampleWriter")
public class ExampleWriter extends AbstractItemWriter {
    // Components
    private final BatchJobItemRepository batchJobItemRepository;

    @Inject
    public ExampleWriter(final BatchJobItemRepository batchJobItemRepository) {
        this.batchJobItemRepository = batchJobItemRepository;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void writeItems(List<Object> items) throws Exception {
        // The items are the individual return values of ExampleProcessor#processItem
        var batchJobItemList = items.stream().map(BatchJobItem.class::cast).toList();
        batchJobItemRepository.saveAll(batchJobItemList);
    }
}
