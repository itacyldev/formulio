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

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecRepo;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecStatus;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionState;
import util.Log;

/**
 * Listener to collect Job execution status updates
 *
 * @autor Javier Ramos (javier.ramos@itacyl.es)
 */
public class JobExecStatusListener {
    private JobProgressActivity activity;
    private JobExecRepo jobExecRepo;
    private final long jobExecId;

    public static final int MSG_CODE = 9085747;
    public static final int RESOURCE_CODE = 93504963;

    public JobExecStatusListener(JobProgressActivity activity, long jobExecId, JobExecRepo jobExecRepo) {
        this.jobExecId = jobExecId;
        this.jobExecRepo = jobExecRepo;
        this.activity = activity;
    }

    private JobExecStatus pollJobStatus(Long jobExecId) {
        JobExecStatus status = jobExecRepo.getJobStatus(jobExecId);
        return status;
    }

    public void startActiveWaiting() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            JobExecStatus status;
            do {
                status = pollJobStatus(jobExecId);
                publishMessages(status);

                try {
                    Log.debug("Waiting...");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (!status.getState().equals(JobExecutionState.FINISHED) && !status.getState().equals(JobExecutionState.ERROR));
            // JOB has finished or execution has stopped because of errors
            publishMessages(status);

            handler.post(() -> {
                activity.endJob();
            });
        });
    }

    /**
     * Send messages to the activity to display them on the screen
     *
     * @param status
     */
    private void publishMessages(JobExecStatus status) {
        List<String> messages = jobExecRepo.getMessages(jobExecId);

        while (messages.size() > 0) {
            String msg = messages.remove(0);
            Log.debug("Updating Message to: " + status.getMessage());
            Message childThreadMessage = new Message();
            childThreadMessage.what = MSG_CODE;
            Bundle msgData = new Bundle();
            msgData.putString("msgTxt", msg);
            childThreadMessage.setData(msgData);
            // Put the message in main thread message queue.
            Handler handler = activity.getMainThreadHandler();
            handler.sendMessage(childThreadMessage);
        }
    }
}
