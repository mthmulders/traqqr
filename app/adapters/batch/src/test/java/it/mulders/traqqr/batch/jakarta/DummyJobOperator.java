package it.mulders.traqqr.batch.jakarta;

import jakarta.batch.operations.JobExecutionAlreadyCompleteException;
import jakarta.batch.operations.JobExecutionIsRunningException;
import jakarta.batch.operations.JobExecutionNotMostRecentException;
import jakarta.batch.operations.JobExecutionNotRunningException;
import jakarta.batch.operations.JobOperator;
import jakarta.batch.operations.JobRestartException;
import jakarta.batch.operations.JobSecurityException;
import jakarta.batch.operations.JobStartException;
import jakarta.batch.operations.NoSuchJobException;
import jakarta.batch.operations.NoSuchJobExecutionException;
import jakarta.batch.operations.NoSuchJobInstanceException;
import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.JobExecution;
import jakarta.batch.runtime.JobInstance;
import jakarta.batch.runtime.StepExecution;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.IntStream;

public class DummyJobOperator implements JobOperator {
    private static final String[] DUMMY_JOB_NAMES = {"example_01", "example_02", "example_03", "example"};

    public record RequestedJobStart(String jobXMLName, Properties jobParameters, Date createTime) {
        RequestedJobStart(String jobXMLName, Properties jobParameters) {
            this(jobXMLName, jobParameters, new Date());
        }
    }

    private final Set<String> knownJobNames;
    private final List<RequestedJobStart> requestedJobStarts = new ArrayList<>();

    public DummyJobOperator() {
        this(DUMMY_JOB_NAMES);
    }

    public DummyJobOperator(String[] knownJobNames) {
        this.knownJobNames = Set.of(knownJobNames);
    }

    @Override
    public Set<String> getJobNames() throws JobSecurityException {
        return knownJobNames;
    }

    @Override
    public int getJobInstanceCount(String jobName) throws NoSuchJobException, JobSecurityException {
        return switch (jobName) {
            case "example_01" -> 1;
            case "example_02" -> 2;
            case "example_03", "example" -> 3;
            default -> 0;
        };
    }

    @Override
    public List<JobInstance> getJobInstances(String jobName, int start, int count)
            throws NoSuchJobException, JobSecurityException {
        if ("example".equals(jobName)) {
            return IntStream.range(start, start + count)
                    .mapToObj(i -> new DummyJobInstance(i, "example"))
                    .map(JobInstance.class::cast)
                    .toList();
        } else {
            return List.of();
        }
    }

    @Override
    public List<Long> getRunningExecutions(String jobName) throws NoSuchJobException, JobSecurityException {
        return List.of();
    }

    @Override
    public Properties getParameters(long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        return null;
    }

    @Override
    public long start(String jobXMLName, Properties jobParameters) throws JobStartException, JobSecurityException {
        this.requestedJobStarts.add(new RequestedJobStart(jobXMLName, jobParameters));
        return this.requestedJobStarts.size();
    }

    @Override
    public long restart(long executionId, Properties restartParameters)
            throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException,
                    JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        return 0;
    }

    @Override
    public void stop(long executionId)
            throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException {
        // Intentionally left empty. Tests do not rely on anything happening here.
    }

    @Override
    public void abandon(long executionId)
            throws NoSuchJobExecutionException, JobExecutionIsRunningException, JobSecurityException {
        // Intentionally left empty. Tests do not rely on anything happening here.
    }

    @Override
    public JobInstance getJobInstance(long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        if (executionId > this.requestedJobStarts.size()) throw new NoSuchJobExecutionException();

        return new DummyJobInstance(executionId, this.requestedJobStarts.get((int) executionId - 1).jobXMLName);
    }

    @Override
    public List<JobExecution> getJobExecutions(JobInstance instance)
            throws NoSuchJobInstanceException, JobSecurityException {
        if ("example".equals(instance.getJobName())) {
            return List.of(new DummyJobExecution(
                    instance.getInstanceId(),
                    instance.getJobName(),
                    BatchStatus.STARTED,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null));
        } else {
            return List.of();
        }
    }

    @Override
    public JobExecution getJobExecution(long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        if (executionId > this.requestedJobStarts.size()) throw new NoSuchJobExecutionException();

        var requestedJobStart = this.requestedJobStarts.get((int) executionId - 1);
        return new DummyJobExecution(
                executionId,
                requestedJobStart.jobXMLName,
                BatchStatus.STARTED,
                null,
                null,
                null,
                requestedJobStart.createTime,
                null,
                requestedJobStart.jobParameters);
    }

    @Override
    public List<StepExecution> getStepExecutions(long jobExecutionId)
            throws NoSuchJobExecutionException, JobSecurityException {
        return List.of();
    }

    public List<RequestedJobStart> getRequestedJobStarts() {
        return requestedJobStarts;
    }
}
