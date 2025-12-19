package it.mulders.traqqr.batch.locationlookup;

import it.mulders.traqqr.domain.batch.BatchJobItem;
import it.mulders.traqqr.domain.batch.spi.BatchJobItemRepository;
import jakarta.batch.api.chunk.AbstractItemWriter;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Dependent
@Named("locationLookupWriter")
public class LocationLookupWriter extends AbstractItemWriter {
    private static final Logger log = LoggerFactory.getLogger(LocationLookupWriter.class);

    // Components
    private final BatchJobItemRepository batchJobItemRepository;

    @Inject
    public LocationLookupWriter(final BatchJobItemRepository batchJobItemRepository) {
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

        log.info("Writing {} items", result.size());
        batchJobItemRepository.saveAll(result);
    }
}
