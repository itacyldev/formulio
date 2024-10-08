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
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecInMemo;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecRepo;
import es.jcyl.ita.formic.jayjobs.jobs.listener.JobExecListener;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionMode;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionState;
import es.jcyl.ita.formic.jayjobs.task.models.Task;

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
            es.jcyl.ita.formic.core.context.Context paramsContext = ctx.getContext("params");
            Context parentContext = (Context) paramsContext.get("parentContext");
            launchProgressActivity(parentContext);
        }
        jobExecRepo.registerExecInit(job, JobExecutionMode.FG);
    }

    private void launchProgressActivity(Context andContext) {
        //Context andContext = App.getInstance().getAndroidContext();
        Intent intent = new Intent(andContext, JobProgressActivity.class);

        intent.putExtra("jobExecId", jobExecId);
        intent.putExtra("jobExecDescription", jobConfig.getDescription());
        andContext.startActivity(intent);
    }


    @Override
    public void onJobEnd(JobConfig job, long jobExecId, JobExecRepo jobExecRepo) {
        jobExecRepo.updateState(jobExecId, JobExecutionState.FINISHED,
                String.format("Job %s has finished!", job.getId()));
    }

    @Override
    public void onJobError(JobConfig job, long jobExecId, JobExecRepo jobExecRepo) {
        jobExecRepo.updateState(jobExecId, JobExecutionState.ERROR,
                String.format("An error occurred on Job  %s execution!", job.getId()));
    }

    @Override
    public void onTaskStart(Task task) {
        jobExecRepo.updateState(jobExecId, JobExecutionState.EXECUTING);
    }

    @Override
    public void onTaskError(Task task, String message, Throwable t) {
        jobExecRepo.updateState(jobExecId, JobExecutionState.ERROR,
                String.format("Task %s has an error", task.getName()));
    }

    @Override
    public void onTaskEnd(Task task) {
        String msg = (task.getDescription() != null) ? task.getDescription() :
                String.format("Task %s has finished", task.getName());
        jobExecRepo.addMessage(jobExecId, msg);
    }

    @Override
    public void onProgressUpdate(Task task, int total, float progress, String units) {
    }

    @Override
    public void onMessage(Task task, String message) {
        jobExecRepo.addMessage(jobExecId, message);
    }
}
