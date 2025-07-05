package it.mulders.traqqr.batch.shared;

import jakarta.batch.api.chunk.listener.AbstractItemProcessListener;
import jakarta.batch.api.chunk.listener.ItemProcessListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic listener to log errors while processing an item.
 * @param <T> The type of item being processed.
 */
public abstract class TraqqrItemProcessListener<T> extends AbstractItemProcessListener implements ItemProcessListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String fieldName;

    protected TraqqrItemProcessListener(final String fieldName) {
        this.fieldName = fieldName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onProcessError(Object item, Exception ex) throws Exception {
        var id = extractId((T) item);
        logger.info("Processing error; {}={}", fieldName, id, ex);
        throw ex;
    }

    protected abstract String extractId(T item);
}
