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
import java.util.Date;

import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecStatus;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecRepo;
import es.jcyl.ita.formic.jayjobs.jobs.listener.JobExecListener;
import es.jcyl.ita.formic.jayjobs.task.models.Task;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class JobProgressListener implements JobExecListener, Serializable {

    private JobProgressActivity activity;
    private JobExecRepo jobExecRepo;
    private Date lastPollTime;

    @Override
    public void onJobStart(JobConfig job, JobExecStatus jobExecInfo, JobExecRepo jobExecRepo) {
        // open activity

        Context andContext = Config.getInstance().getAndroidContext();
        Intent intent = new Intent(andContext, JobProgressActivity.class);
        intent.putExtra("jobListener", this);
        andContext.startActivity(intent);
    }

    public void setActivity(JobProgressActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onJobEnd(JobConfig job, JobExecStatus jobExecInfo, JobExecRepo jobExecRepo) {
        if (activity != null) {
            activity.setMessage("info", String.format("Job %s has finished!", job.getId()));
//            JobFacade facade;
//            activity.setResources(facade.getResources(jobExecInfo.getId()));
        }
    }

    @Override
    public void onJobError(JobConfig job, JobExecStatus jobExecInfo, JobExecRepo jobExecRepo) {
        if (activity != null) {
            activity.setMessage("info", String.format("An error occurred on Job  %s execution!", job.getId()));
        }
    }

    @Override
    public void onTaskStart(Task task) {
        if (activity != null) {
            activity.setMessage("info", String.format("Task %s has started", task.getName()));
        }
    }

    @Override
    public void onTaskError(Task task, String message, Throwable t) {
        if (activity != null) {
            activity.setMessage("error", String.format("Task %s has an error", task.getName()));
        }
    }

    @Override
    public void onTaskEnd(Task task) {
        if (activity != null) {
            activity.setMessage("info", String.format("Task %s has finished", task.getName()));
        }
    }

    @Override
    public void onProgressUpdate(Task task, int total, float progress, String units) {
        if (activity != null) {
            activity.setMessage("info", "" + progress);
        }
    }

    @Override
    public void onMessage(Task task, String message) {
        activity.setMessage("info", message);
    }


    public void pollState(Long jobExecId){
        JobExecStatus status = jobExecRepo.getJobStatus(jobExecId);

    }

}
