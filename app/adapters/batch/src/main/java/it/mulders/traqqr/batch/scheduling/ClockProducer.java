package it.mulders.traqqr.batch.scheduling;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import java.time.Clock;

@ApplicationScoped
public class ClockProducer {
    @ApplicationScoped
    @Produces
    public Clock clock() {
        return Clock.systemUTC();
    }
}
