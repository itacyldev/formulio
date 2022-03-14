package es.jcyl.ita.formic.jayjobs.task.listener;

import es.jcyl.ita.formic.jayjobs.task.models.Task;
import util.Log;

public class LogTaskListener implements TaskExecListener {

    @Override
    public void onTaskStart(Task task) {
        Log.info(String.format(">>> Task %s | Task started.", task.getName()));
    }

    @Override
    public void onTaskError(Task task, String message, Throwable t) {
        Log.error(String.format(">>> Task %s | Error on task:. \n %s", task.getName(), t.getMessage()));
    }

    @Override
    public void onTaskEnd(Task task) {
        Log.info(String.format(">>> Task %s | Task finished.", task.getName()));
    }

    @Override
    public void onMessage(Task task, String message) {
        Log.info(String.format(">>> Task %s | %s", task.getName(), message));
    }

    @Override
    public void onProgressUpdate(Task task, int total, float progress, String units) {
        Log.info(String.format(">>> Task %s | Step %s of %s.", task.getName(), total, progress));
    }
}
