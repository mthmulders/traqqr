package it.mulders.traqqr.batch.scheduling;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DummyBean {
    private static final Logger log = LoggerFactory.getLogger(DummyBean.class);

    private int invocationCount = 0;

    @Scheduled(dayOfMonth = "*", hour = "*", minute = "*")
    public void performAction() {
        log.info("Performing action");
        invocationCount++;
    }

    public int getInvocationCount() {
        return invocationCount;
    }
}
