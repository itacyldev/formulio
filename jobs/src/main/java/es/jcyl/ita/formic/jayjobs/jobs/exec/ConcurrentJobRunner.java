package es.jcyl.ita.formic.jayjobs.jobs.exec;

import java.util.concurrent.Executor;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.exception.JobException;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.executor.TaskExecutor;
import util.Log;

public class ConcurrentJobRunner extends AbstractJobRunner implements JobRunner {

    private TaskExecutor taskExecutor = new TaskExecutor();
    private final Executor threadExecutor;

    public ConcurrentJobRunner(Executor executor) {
        this.threadExecutor = executor;
    }

    @Override
    public void execute(CompositeContext ctx, JobConfig job, JobExecStatus jobExecInfo, JobExecRepo jobExecRepo) throws JobException {
        threadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                notifyStart(job, jobExecInfo, jobExecRepo);
                taskExecutor.setListener(listener);
                try {
                    taskExecutor.execute(ctx, job.getTaskConfig());
                    notifyEnd(job, jobExecInfo, jobExecRepo);
                } catch (TaskException e) {
                    notifyError(ctx, job, jobExecInfo, jobExecRepo);
                    String msg = String.format(
                            "An error occurred during the execution id [%s] of the job [%s].",
                            jobExecInfo.getId(), job.getId());
                    Log.error(msg, e);
                }
            }
        });
    }
}
