package it.mulders.traqqr.batch.scheduling;

public interface Scheduler {
    void schedule(Schedule schedule, Runnable runnable);
}
