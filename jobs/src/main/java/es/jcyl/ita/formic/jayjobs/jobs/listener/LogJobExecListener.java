package es.jcyl.ita.formic.jayjobs.jobs.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecStatus;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecRepo;
import es.jcyl.ita.formic.jayjobs.task.models.Task;

public class LogJobExecListener implements JobExecListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogJobExecListener.class);
    @Override
    public void onTaskStart(Task task) {
        LOGGER.info(String.format(">>> Task %s | Task started.", task.getName()));
    }

    @Override
    public void onTaskError(Task task, String message, Throwable t) {
        LOGGER.error(String.format(">>> Task %s | Error on task:. \n %s", task.getName(), t.getMessage()));
    }

    @Override
    public void onTaskEnd(Task task) {
        LOGGER.info(String.format(">>> Task %s | Task finished.", task.getName()));
    }

    @Override
    public void onMessage(Task task, String message) {
        LOGGER.info(String.format(">>> Task %s | %s", task.getName(), message));
    }

    @Override
    public void onProgressUpdate(Task task, int total, float progress, String units) {
        LOGGER.info(String.format(">>> Task %s | Step %s of %s.", task.getName(), total, progress));
    }

    @Override
    public void onJobStart(CompositeContext ctx, JobConfig job, long jobExecId, JobExecRepo jobExecRepo) {
        JobExecStatus status = jobExecRepo.getJobStatus(jobExecId);
        LOGGER.info(String.format("===== JOB %s STARTED ExecId: %s =====", job.getId(), status.getExecInit()));
    }

    @Override
    public void onJobEnd(JobConfig job, long jobExecId, JobExecRepo jobExecRepo) {
        JobExecStatus status = jobExecRepo.getJobStatus(jobExecId);
        LOGGER.info(String.format("===== JOB %s END ExecId: %s =====", job.getId(), status.getExecInit()));
    }

    @Override
    public void onJobError(JobConfig job, long jobExecId, JobExecRepo jobExecRepo) {
        JobExecStatus status = jobExecRepo.getJobStatus(jobExecId);
        LOGGER.info(String.format("===== Error on job %s END ExecId: %s =====", job.getId(), status.getExecInit()));
    }
}
