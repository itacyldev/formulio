package es.jcyl.ita.formic.app.jobs;
/*
 * Copyright 2022 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.List;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.app.dialog.ProgressDialog;
import es.jcyl.ita.formic.forms.view.activities.BaseActivity;
import es.jcyl.ita.formic.jayjobs.jobs.exception.JobException;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecInMemo;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecRepo;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobResource;

/**
 * Activity to show the execution of a job and its results
 *
 * @autor Javier Ramos (javier.ramos@itacyl.es)
 */
public class JobProgressActivity extends BaseActivity {
    private final Activity activity = this;

    long jobId;

    JobExecRepo jobExecRepo;

    private JobExecStatusListener execStatusListener;

    private JobProgressHandler mainThreadHandler;

    public JobProgressHandler getMainThreadHandler() {
        return mainThreadHandler;
    }

    ProgressDialog progressDialog;

    @Override
    protected void doOnCreate() {
        setContentView(R.layout.job_progress);
        jobId = getIntent().getLongExtra("jobExecId", -1);
        jobExecRepo = JobExecInMemo.getInstance();
        execStatusListener = new JobExecStatusListener(this, jobId, jobExecRepo);


        progressDialog = new ProgressDialog(activity);
        progressDialog.show();

        mainThreadHandler = new JobProgressHandler();

        execStatusListener.startActiveWaiting();
    }

    public void setMessage(String msg) {
        progressDialog.setText(msg);
    }


    @Override
    public void onBackPressed() {
        // Do nothing
    }

    public void endJob() {
        publishResources();
        progressDialog.showProgressButton();
    }

    private void publishResources() {
        try {
            List<JobResource> resources = jobExecRepo.getResources(jobId);
            for (JobResource resource : resources) {
                String resourcePath = resource.getResourcePath();
                addResource(resourcePath);
            }
        } catch (JobException e) {

        }
    }




    public void addResource(String resourcePath) {
        progressDialog.addResource(resourcePath);
    }


    private class JobProgressHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle msgData = msg.getData();
            String msgText = msgData.getString("msgTxt");
            if (msg.what == JobExecStatusListener.MSG_CODE) {
                setMessage(msgText);
            }
        }
    }
}


