package it.mulders.traqqr.batch.scheduling;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DummyBean {
    private int invocationCount = 0;

    public void performAction() {
        invocationCount++;
    }

    public int getInvocationCount() {
        return invocationCount;
    }
}
