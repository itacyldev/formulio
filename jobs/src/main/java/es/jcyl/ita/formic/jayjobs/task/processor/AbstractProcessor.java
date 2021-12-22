package es.jcyl.ita.formic.jayjobs.task.processor;

import es.jcyl.ita.crtdrd.task.TaskProgressNotifier;
import es.jcyl.ita.formic.jayjobs.task.models.AbstractTaskSepItem;

public abstract class AbstractProcessor extends AbstractTaskSepItem {
	protected TaskProgressNotifier notifier;

	private boolean failOnError = true;

	public boolean isFailOnError() {
		return this.failOnError;
	}

	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

	public void setNotifier(TaskProgressNotifier notifier){
		this.notifier = notifier;
	}

	public TaskProgressNotifier getNotifier(){
		return this.notifier;
	}

}
