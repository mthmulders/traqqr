package it.mulders.traqqr.batch.example;

import it.mulders.traqqr.domain.batch.BatchJobItem;
import it.mulders.traqqr.domain.batch.BatchJobItemRepository;
import jakarta.batch.api.chunk.AbstractItemWriter;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
@Named("exampleWriter")
public class ExampleWriter extends AbstractItemWriter {
    private static final Logger log = LoggerFactory.getLogger(ExampleWriter.class);

    // Components
    private final BatchJobItemRepository batchJobItemRepository;

    @Inject
    public ExampleWriter(final BatchJobItemRepository batchJobItemRepository) {
        this.batchJobItemRepository = batchJobItemRepository;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void writeItems(List<Object> items) {
        // The items are the individual return values of ExampleProcessor#processItem
        var result = new ArrayList<BatchJobItem<?>>();

        // Not using the Streams API because it has trouble inferring the type
        // of the Collector to use.
        items.forEach(item -> {
            if (item instanceof BatchJobItem<?> batchJobItem) {
                result.add(batchJobItem);
            } else {
                log.warn("Unexpected item type {}", item.getClass());
            }
        });

        batchJobItemRepository.saveAll(result);
    }
}
