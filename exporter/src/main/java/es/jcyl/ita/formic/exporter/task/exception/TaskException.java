package es.jcyl.ita.formic.exporter.task.exception;

public class TaskException extends Exception {

	public TaskException(String msg, Throwable t) {
		super(msg, t);
	}

	public TaskException(Throwable t) {
		super(t);
	}

	public TaskException(String msg) {
		super(msg);
	}
}
