package es.jcyl.ita.formic.jayjobs.task.notifier;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.jcyl.ita.crtcyl.sync.service.SyncService;
import es.jcyl.ita.crtdrd.task.TaskProgressNotifier;
import es.jcyl.ita.formic.jayjobs.R;
import es.jcyl.ita.formic.jayjobs.task.processor.Processor;

/**
 * Created by ita-ramvalja on 07/05/2019.
 */

public class TaskStatusNotifier implements TaskProgressNotifier {

    protected static final Log LOGGER = LogFactory
            .getLog(TaskStatusNotifier.class);

    private Context ctx;

    private AlertDialog progressDialog;
    private AlertDialog errorDialog;

    private Processor processor;

    public TaskStatusNotifier(Context ctx) {
        this.ctx = ctx;

        this.init();
    }

    private void init() {
        createProgressDialog();

        createErrorDialog();
    }

    private void createProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Synchronization");
        builder.setCancelable(false);
        progressDialog = builder.create();
        progressDialog.setCanceledOnTouchOutside(false);

    }

    private void createErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Synchronization");
        builder.setCancelable(false);
        builder.setPositiveButton("Accept", new
                DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        errorDialog = builder.create();
        errorDialog.setCanceledOnTouchOutside(false);
    }

    public void notifyError(Long taskid, String statusError, final String
            errorMessage) {
        progressDialog.dismiss();
        LOGGER.debug(String.format("Error en tarea %s: %s", taskid,
                errorMessage));
        ((Activity) ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showErrorMsg(null, errorMessage);
            }
        });
    }

    public void notify(int status, String message) {

    }

    @Override
    public void setTaskSize(Long aLong, Long aLong1) {

    }

    @Override
    public void updateDetailedStatus(Long taskId, final Integer status,
                                     String message) {
        LOGGER.debug(String.format("Tarea %s: %s", taskId,
                message));

        ((Activity) ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgressMsg(status);
            }
        });
    }

    @Override
    public void updateProgress(Long taskId, Long progress) {
        showProgressMsg(progress.intValue());
    }

    @Override
    public void taskEnd(Long aLong) {
        progressDialog.dismiss();
    }

    @Override
    public void setSyncId(Integer integer) {

    }

    @Override
    public Integer getSyncId() {
        return null;
    }


    private void showProgressMsg(Integer progress) {
        String message = "";
        SyncService.SyncStates stateView = SyncService.SyncStates.values()
                [progress];
        switch (stateView) {
            case CHECKING_CONNECTIVIY:
                message = ctx
                        .getString(R.string.sync_checkin_connectivity);
                break;
            case COMPRESSING_FILE:
                message = ctx
                        .getString(R.string.sync_compressing_files);
                break;
            case CREATING_TASK:
                message = ctx
                        .getString(R.string.sync_creating_task);
                break;
            case UPLOADINGING_FILE:
                message = ctx
                        .getString(R.string.sync_uploading_files);
                break;
            case FILES_UPLOADED:
                message = ctx
                        .getString(R.string.sync_files_uploaded);
                break;
            case DOWNLOAD_FILE_READY:
                message = ctx
                        .getString(R.string.sync_downloading_files);
                break;
            case FILES_DOWNLOADED:
                message = ctx
                        .getString(R.string.sync_files_downloaded);
                break;
            case SYNC_FINISHED:
                progressDialog.dismiss();
                this.finishSync();
        }

        if (!StringUtils.isEmpty(message)) {
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }


    private void showErrorMsg(Integer progress, String message) {
        if (message != null) {
            errorDialog.setMessage(message);
            errorDialog.show();
        } else {
            errorDialog.dismiss();
        }
    }


    private void finishSync() {
        processor.finish();

    }

    public void setClient(Processor processor) {
        this.processor = processor;
    }
}
