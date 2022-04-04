package es.jcyl.ita.formic.core.context;

import org.apache.commons.jexl3.MapContext;
import org.apache.commons.text.StringSubstitutor;

import java.util.Map;

import es.jcyl.ita.formic.core.jexl.JexlUtils;

public class VarSubstitutor {

    public static String replace(String source, Map values) {
        return String.valueOf(JexlUtils.eval(new MapContext(values), source));
    }

    public static String replaceDynamic(String source, Map values) {
        return String.valueOf(JexlUtils.eval(new MapContext(values), source));
    }
//
//	public static String replace(Object source, final Context values) {
//		String str = (String) source;
//
//		StrSubstitutor sst = new StrSubstitutor(new StrLookup() {
//			@Override
//			public String lookup(String key) {
//				JexlExpression expr = CustomJexlBuilder.build()
//						.createExpression(key);
//				return expr.evaluate((JexlContext) values).toString();
//			}
//		}, JEXL_DEFAULT_PREFIX, JEXL_DEFAULT_SUFFIX, JEXL_DEFAULT_ESCAPE);
//
//		str = sst.replace(source);
//		return StrSubstitutor.replace(str, values);
//	}
}
