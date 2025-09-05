package it.mulders.traqqr.batch.scheduling;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.Executors;

@ApplicationScoped
public class TestScheduler extends DefaultSimpleScheduler {
    public TestScheduler() {
        super(Clock.systemUTC(), Duration.ofMillis(10_000), Executors.newFixedThreadPool(5));
    }
}
