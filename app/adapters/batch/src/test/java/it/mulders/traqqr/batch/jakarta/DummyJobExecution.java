package it.mulders.traqqr.batch.jakarta;

import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.JobExecution;
import java.util.Date;
import java.util.Properties;

public class DummyJobExecution implements JobExecution {
    private final long executionId;
    private final String jobName;
    private final BatchStatus batchStatus;
    private final Date startTime;
    private final Date endTime;
    private final String exitStatus;
    private final Date createTime;
    private final Date lastUpdatedTime;
    private final Properties jobParameters;

    public DummyJobExecution(
            long executionId,
            String jobName,
            BatchStatus batchStatus,
            Date startTime,
            Date endTime,
            String exitStatus,
            Date createTime,
            Date lastUpdatedTime,
            Properties jobParameters) {
        this.executionId = executionId;
        this.jobName = jobName;
        this.batchStatus = batchStatus;
        this.startTime = startTime;
        this.endTime = endTime;
        this.exitStatus = exitStatus;
        this.createTime = createTime;
        this.lastUpdatedTime = lastUpdatedTime;
        this.jobParameters = jobParameters;
    }

    @Override
    public long getExecutionId() {
        return executionId;
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    @Override
    public BatchStatus getBatchStatus() {
        return batchStatus;
    }

    @Override
    public Date getStartTime() {
        return startTime;
    }

    @Override
    public Date getEndTime() {
        return endTime;
    }

    @Override
    public String getExitStatus() {
        return exitStatus;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    @Override
    public Properties getJobParameters() {
        return jobParameters;
    }
}
