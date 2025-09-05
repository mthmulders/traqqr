package it.mulders.traqqr.batch.scheduling;

import static java.util.Comparator.comparing;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DefaultSimpleScheduler implements Scheduler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultSimpleScheduler.class);

    // Components
    @Inject
    private Clock clock;

    @Resource
    private ExecutorService executor;

    private final NextInvocationTimeCalculator nextInvocationTimeCalculator = new NextInvocationTimeCalculator();
    private final TimingWorker timingWorker = new TimingWorker();

    // Data
    private final List<ScheduledMethod> pendingInvocations = new LinkedList<>();

    // Configuration
    private final Duration sleepDuration;

    public DefaultSimpleScheduler() {
        this.sleepDuration = Duration.ofMinutes(1);
    }

    protected DefaultSimpleScheduler(final Clock clock, final Duration sleepDuration, final ExecutorService executor) {
        this.clock = clock;
        this.sleepDuration = sleepDuration;
        this.executor = executor;
    }

    public void startBackgroundThread(@Observes @Initialized(ApplicationScoped.class) Object ignored) {
        logger.info("Starting background thread for scheduler");
        executor.submit(timingWorker);
    }

    private class TimingWorker implements Runnable {
        private static final Logger logger = LoggerFactory.getLogger(TimingWorker.class);

        @Override
        @SuppressWarnings("java:S2189") // the 'while true' will terminate when the thread is interrupted
        public void run() {
            //
            // Super-naive: check at a fixed interval if there's work to do.
            //

            while (true) {
                if (!pendingInvocations.isEmpty()) {
                    var now = OffsetDateTime.now(clock);
                    var next = pendingInvocations.getFirst();
                    var nextInvocation = next.timestamp;
                    var wakeup = now.plus(sleepDuration);

                    logger.debug(
                            "Inspecting first scheduled task; now={}, next_invocation={}, next_wakeup={}",
                            now,
                            nextInvocation,
                            wakeup);

                    if (wakeup.isAfter(nextInvocation)) {
                        logger.debug("First scheduled task is due or overdue, executing it; delegate={}", next.delegate);
                        executor.submit(next);
                        pendingInvocations.remove(next);

                        schedule(next.schedule, next.delegate);
                    } else {
                        logger.debug("Nothing to do; now={}, next_invocation={}, next_wakeup={}", now, nextInvocation, wakeup);
                    }
                }

                try {
                    Thread.sleep(sleepDuration);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    public void stopBackgroundThread(@Observes @Destroyed(ApplicationScoped.class) Object ignored) {
        logger.info("Stopping background thread for scheduler");
        executor.shutdown();
    }

    @Override
    public void schedule(final Schedule schedule, final Runnable runnable) {
        logger.debug("Scheduling next invocation; method={}", runnable);

        var now = OffsetDateTime.now(clock);
        var nextInvocation = nextInvocationTimeCalculator.calculateNextInvocationTime(now, schedule);

        var scheduledMethod = new ScheduledMethod(runnable, nextInvocation, schedule);
        pendingInvocations.add(scheduledMethod);

        pendingInvocations.sort(comparing(ScheduledMethod::timestamp));
    }

    private record ScheduledMethod(Runnable delegate, OffsetDateTime timestamp, Schedule schedule) implements Runnable {

        @Override
        public void run() {
            delegate.run();
        }
    }
}
