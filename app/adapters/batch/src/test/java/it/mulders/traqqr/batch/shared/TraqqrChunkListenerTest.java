package it.mulders.traqqr.batch.shared;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TraqqrChunkListenerTest implements WithAssertions {
    private final TraqqrChunkListener listener = new TraqqrChunkListener();

    @Test
    void beforeChunk_should_not_crash() throws Exception {
        listener.beforeChunk();
    }

    @Test
    void onError_should_rethrow_Exception() {
        var ex = new Exception();
        assertThatThrownBy(() -> listener.onError(ex)).isEqualTo(ex);
    }
}
