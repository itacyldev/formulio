package es.jcyl.ita.formic.app.jobs;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.Date;

import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecRepo;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecStatus;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionState;

public class JobExecStatusListener {
    private JobProgressActivity activity;
    private JobExecRepo jobExecRepo;
    private final long jobExecId;

    private Date lastPollTime;

    public static final int MSG_CODE = 1;

    public JobExecStatusListener(JobProgressActivity activity, long jobExecId){
        this.jobExecId = jobExecId;
        this.activity = activity;
    }

    private JobExecStatus pollJobStatus(Long jobExecId) {
        JobExecStatus status = jobExecRepo.getJobStatus(jobExecId);
        return status;
    }

    public void startActiveWaiting() {
        lastPollTime = new Date();
        final Thread thread = new Thread() {
            @Override
            public void run() {
                JobExecStatus status;
                do {
                    status = pollJobStatus(jobExecId);
                    if (status.getLastTimeUpdated().after(lastPollTime)) {
                        // Create a message in child thread.
                        Message childThreadMessage = new Message();
                        childThreadMessage.what = MSG_CODE;
                        Bundle msgData = new Bundle();
                        msgData.putString("msgTxt", status.getMessage());
                        childThreadMessage.setData(msgData);
                        // Put the message in main thread message queue.
                        Handler handler = activity.getMainThreadHandler();
                        handler.sendMessage(childThreadMessage);
                    }

                    try {
                        sleep(1000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (status.getState().equals(JobExecutionState.EXECUTING));
            }
        };

        thread.start();

    }

}
