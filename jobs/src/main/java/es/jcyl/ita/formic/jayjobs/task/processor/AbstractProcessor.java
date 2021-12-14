package es.jcyl.ita.formic.jayjobs.task.processor;

import es.jcyl.ita.formic.jayjobs.task.models.AbstractTaskSepItem;

public abstract class AbstractProcessor extends AbstractTaskSepItem {
	private boolean failOnError = true;

	public boolean isFailOnError() {
		return this.failOnError;
	}

	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

}
