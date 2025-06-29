package it.mulders.traqqr.batch.shared;

import jakarta.batch.api.chunk.listener.AbstractChunkListener;
import jakarta.batch.api.chunk.listener.ChunkListener;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
@Named("traqqrChunkListener")
public class TraqqrChunkListener extends AbstractChunkListener implements ChunkListener {
    private static final Logger log = LoggerFactory.getLogger(TraqqrChunkListener.class);

    private int chunkCount = 0;

    @Override
    public void beforeChunk() throws Exception {
        super.beforeChunk();
        chunkCount++;
    }

    @Override
    public void onError(Exception ex) throws Exception {
        log.info("Error processing chunk; chunk_num={}", chunkCount, ex);
        throw ex;
    }
}
