package it.mulders.traqqr.batch.jakarta;

import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.context.JobContext;
import java.util.Properties;

public class DummyJobContext implements JobContext {
    private final Properties props = new Properties();

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
        return 0;
    }

    @Override
    public long getExecutionId() {
        return 0;
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
