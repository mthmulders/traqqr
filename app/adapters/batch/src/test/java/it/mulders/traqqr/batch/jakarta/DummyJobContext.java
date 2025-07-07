package it.mulders.traqqr.batch.jakarta;

import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.context.JobContext;
import java.util.Properties;

public class DummyJobContext implements JobContext {
    private final Properties props = new Properties();

    private final int jobExecutionId;
    private final int jobInstanceId;

    public DummyJobContext(int jobExecutionId, int jobInstanceId) {
        this.jobExecutionId = jobExecutionId;
        this.jobInstanceId = jobInstanceId;
    }

    public DummyJobContext() {
        this(0, 0);
    }

    @Override
    public String getJobName() {
        return "";
    }

    @Override
    public Object getTransientUserData() {
        return null;
    }

    @Override
    public void setTransientUserData(Object data) {}

    @Override
    public long getInstanceId() {
        return jobInstanceId;
    }

    @Override
    public long getExecutionId() {
        return jobExecutionId;
    }

    @Override
    public Properties getProperties() {
        return props;
    }

    @Override
    public BatchStatus getBatchStatus() {
        return null;
    }

    @Override
    public String getExitStatus() {
        return "";
    }

    @Override
    public void setExitStatus(String status) {}
}
