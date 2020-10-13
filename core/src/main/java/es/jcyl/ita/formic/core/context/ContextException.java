package es.jcyl.ita.formic.core.context;


public class ContextException extends RuntimeException {
	private static final long serialVersionUID = -3599813072560026919L;

	public ContextException() {
		super();
	}

	public ContextException(String msg) {
		super(msg);
	}

	public ContextException(Throwable t) {
		super(t);
	}

	public ContextException(String msg, Throwable t) {
		super(msg, t);
	}

}
