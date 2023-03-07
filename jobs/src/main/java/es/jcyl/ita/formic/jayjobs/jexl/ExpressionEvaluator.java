package es.jcyl.ita.formic.jayjobs.jexl;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlExpression;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.jexl.JexlUtils;
import es.jcyl.ita.formic.jayjobs.jexl.helpers.Caster;


public class ExpressionEvaluator {

	private ExpressionEvaluator() {
	}

	private static Caster caster = new Caster();

	public static boolean evalAsBool(Context context, String expression) {
		return caster.bool(eval(context, expression));
	}

	public static float evalAsFloat(Context context, String expression) {
		return caster.decimal(eval(context, expression));
	}

	public static int evalAsInt(Context context, String expression) {
		return caster.integer(eval(context, expression));
	}

	public static Object eval(Context context, String expression) {
		return JexlUtils.eval(context, expression);
	}
}
