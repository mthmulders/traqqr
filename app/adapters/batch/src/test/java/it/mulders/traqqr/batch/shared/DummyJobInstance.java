package it.mulders.traqqr.batch.shared;

import jakarta.batch.runtime.JobInstance;

public class DummyJobInstance implements JobInstance {
    private final long instanceId;
    private final String jobName;

    public DummyJobInstance(long instanceId, String jobName) {
        this.instanceId = instanceId;
        this.jobName = jobName;
    }

    @Override
    public long getInstanceId() {
        return instanceId;
    }

    @Override
    public String getJobName() {
        return jobName;
    }
}
