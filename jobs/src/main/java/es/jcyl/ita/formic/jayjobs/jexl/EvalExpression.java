package es.jcyl.ita.formic.jayjobs.jexl;

public class EvalExpression {
	private String expression;
	private String messageType = "INFO";
	private String message;

	public EvalExpression() {
	}

	public EvalExpression(String expression, String msgType, String message) {
		this.expression = expression;
		messageType = msgType;
		this.message = message;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

}
