package es.jcyl.ita.formic.app.jobs;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.forms.view.activities.FormEditViewHandlerActivity;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.exception.JobException;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecInMemo;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecRepo;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecStatus;
import es.jcyl.ita.formic.jayjobs.jobs.listener.JobExecListener;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionMode;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionState;
import es.jcyl.ita.formic.jayjobs.task.models.Task;
import util.Log;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class JobProgressListener implements JobExecListener, Serializable {

    private JobExecRepo jobExecRepo;
    private JobConfig jobConfig;
    private long jobExecId;

    private boolean showProgress = true;

    public JobProgressListener() {
        this.jobExecRepo = JobExecInMemo.getInstance();
    }

    public JobProgressListener(boolean showProgress) {
        this.showProgress = showProgress;
        this.jobExecRepo = JobExecInMemo.getInstance();
    }

    @Override
    public void onJobStart(CompositeContext ctx, JobConfig job, long jobExecId, JobExecRepo jobExecRepo) {
        this.jobExecId = jobExecId;
        this.jobConfig = job;

        // open activity
        if (showProgress) {
            es.jcyl.ita.formic.core.context.Context paramsContext =  ctx.getContext("params");
            Context  parentContext = (Context) paramsContext.get("parentContext");
            launchProgressActivity(parentContext);
        }

        try {
            jobExecRepo.registerExecInit(job, JobExecutionMode.FG);
        } catch (JobException ex) {
            Log.error(String.format("An error occurred during execution of job [%s]", job.getId()));
        }
    }

    private void launchProgressActivity(Context andContext) {
        //Context andContext = App.getInstance().getAndroidContext();
        Intent intent = new Intent(andContext, JobProgressActivity.class);

        intent.putExtra("jobExecId", jobExecId);
        andContext.startActivity(intent);
    }


    @Override
    public void onJobEnd(JobConfig job, long jobExecId, JobExecRepo jobExecRepo) {
        JobExecStatus status = jobExecRepo.getJobStatus(jobExecId);
        status.setState(JobExecutionState.FINISHED);
        status.setMessage(String.format("Job %s has finished!", job.getId()));
        jobExecRepo.updateJobStatus(status);
    }

    @Override
    public void onJobError(JobConfig job, long jobExecId, JobExecRepo jobExecRepo) {
        JobExecStatus status = jobExecRepo.getJobStatus(jobExecId);
        status.setState(JobExecutionState.FINISHED);
        status.setMessage(String.format("An error occurred on Job  %s execution!", job.getId()));
        jobExecRepo.updateJobStatus(status);
    }

    @Override
    public void onTaskStart(Task task) {
        JobExecStatus status = jobExecRepo.getJobStatus(jobExecId);
        status.setState(JobExecutionState.EXECUTING);
        status.setMessage(String.format("Task %s has started", task.getName()));
        jobExecRepo.updateJobStatus(status);
    }

    @Override
    public void onTaskError(Task task, String message, Throwable t) {
        JobExecStatus status = jobExecRepo.getJobStatus(jobExecId);
        status.setMessage(String.format("Task %s has an error", task.getName()));
        status.setState(JobExecutionState.ERROR);
        jobExecRepo.updateJobStatus(status);
    }

    @Override
    public void onTaskEnd(Task task) {
        JobExecStatus status = jobExecRepo.getJobStatus(jobExecId);
        status.setMessage(String.format("Task %s has finished", task.getName()));
        jobExecRepo.updateJobStatus(status);
    }

    @Override
    public void onProgressUpdate(Task task, int total, float progress, String units) {
    }

    @Override
    public void onMessage(Task task, String message) {
        JobExecStatus status = jobExecRepo.getJobStatus(jobExecId);
        status.setMessage(message);
        jobExecRepo.updateJobStatus(status);
    }


}
