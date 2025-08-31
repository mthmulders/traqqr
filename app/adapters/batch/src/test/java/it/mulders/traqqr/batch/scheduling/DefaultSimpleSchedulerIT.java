package it.mulders.traqqr.batch.scheduling;

import it.mulders.clocky.AdvanceableTime;
import it.mulders.clocky.ManualClock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DefaultSimpleSchedulerIT implements WithAssertions {
    private final AdvanceableTime time = new AdvanceableTime(Instant.EPOCH);
    private final ManualClock clock = new ManualClock(time);

    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    private final DefaultSimpleScheduler scheduler =
            new DefaultSimpleScheduler(clock, Duration.ofMillis(500), executor);

    @BeforeEach
    void startExecutor() {
        scheduler.startBackgroundThread(null);
    }

    @AfterEach
    void stopExecutor() {
        scheduler.stopBackgroundThread(null);
    }

    /**
     * Verifies that a method invocation is indeed executed. The test schedules a method to run every minute,
     * advances *fake* time by a minute and then sleeps for a second. This is long enough to give the timing worker
     * two chances at picking up the scheduled task.
     */
    @Test
    void submitting_a_method_invocation_should_execute_it() throws InterruptedException {
        // Arrange
        var invocationCount = new AtomicInteger(0);
        Runnable action = invocationCount::incrementAndGet;

        // Act
        assertThat(invocationCount.get()).isEqualTo(0);
        scheduler.schedule(new Schedule("*", "*", "*"), action);
        time.advanceBy(Duration.ofMinutes(1)); // fake time
        Thread.sleep(Duration.ofSeconds(1)); // real time

        // Assert
        assertThat(invocationCount.get()).isEqualTo(1);
    }
}
