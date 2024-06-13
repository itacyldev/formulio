package es.jcyl.ita.formic.jayjobs.jexl;

public class JexlExpressionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JexlExpressionException(String msg, Throwable t) {
		super(msg, t);
	}

	public JexlExpressionException(Throwable t) {
		super(t);
	}

	public JexlExpressionException(String msg) {
		super(msg);
	}

}
