package es.jcyl.ita.formic.app.jobs;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.Date;

import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecRepo;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecStatus;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionState;
import util.Log;

public class JobExecStatusListener {
    private JobProgressActivity activity;
    private JobExecRepo jobExecRepo;
    private final long jobExecId;

    private Date lastPollTime;

    public static final int MSG_CODE = 9085747;

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

        final Thread thread = new Thread() {
            @Override
            public void run() {
                JobExecStatus status;
                do {
                    status = pollJobStatus(jobExecId);

                    if (lastPollTime == null || (status.getLastTimeUpdated() != null && status.getLastTimeUpdated().after(lastPollTime))) {
                        // Create a message in child thread.
                        if (status.getMessage() != null) {
                            Log.debug("Updating Message to: " + status.getMessage());
                            Message childThreadMessage = new Message();
                            childThreadMessage.what = MSG_CODE;
                            Bundle msgData = new Bundle();
                            msgData.putString("msgTxt", status.getMessage());
                            childThreadMessage.setData(msgData);
                            // Put the message in main thread message queue.
                            Handler handler = activity.getMainThreadHandler();
                            handler.sendMessage(childThreadMessage);
                        }
                        lastPollTime = new Date();
                    }


                    try {
                        Log.debug("Waiting...");
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (!status.getState().equals(JobExecutionState.FINISHED) && !status.getState().equals(JobExecutionState.ERROR));
                // JOB has finished or execution has stopped because of errors
            }
        };

        thread.start();

//        try {
//            thread.join();
//        }catch (InterruptedException e){
//            Log.error("Error waiting thread finish");
//        }


        activity.endJob();
    }

}
