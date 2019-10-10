package es.jcyl.ita.frmdrd.context;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;

public class JexlTest {

    public static Integer test() {
        JexlBuilder builder = new JexlBuilder();
        JexlEngine jexl = builder.cache(512).strict(true).silent(false).create();
        JexlExpression e = jexl.createExpression("$1 + $2");

        JexlContext context = new MapContext();
        context.set("$1", 1);
        context.set("$2", 2);

        Integer result = (Integer) e.evaluate(context);

        return result;
    }

}
