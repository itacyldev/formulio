package es.jcyl.ita.formic.jayjobs.task.processor;

import es.jcyl.ita.formic.jayjobs.task.listener.TaskListener;
import es.jcyl.ita.formic.jayjobs.task.models.AbstractTaskSepItem;

public abstract class AbstractProcessor extends AbstractTaskSepItem {
    protected TaskListener listener;

    private boolean failOnError = true;

    public boolean isFailOnError() {
        return this.failOnError;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public void setListener(TaskListener listener) {
        this.listener = listener;
    }

    @Override
    public TaskListener getListener() {
        return listener;
    }
}
