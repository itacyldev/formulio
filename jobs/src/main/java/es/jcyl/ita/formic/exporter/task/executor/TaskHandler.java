package es.jcyl.ita.formic.exporter.task.executor;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.exporter.task.models.Task;
import es.jcyl.ita.formic.exporter.task.exception.TaskException;

public interface TaskHandler<T extends Task> {

	void handle(CompositeContext context, T task, TaskExec taskExecutionInfo) throws TaskException;
}
