package es.jcyl.ita.formic.forms.el;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JxltEngine;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.internal.Engine;
import org.apache.commons.jexl3.internal.TemplateEngine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.context.impl.ComponentContext;
import es.jcyl.ita.formic.forms.el.wrappers.JexlContextWrapper;
import es.jcyl.ita.formic.forms.el.wrappers.JexlEntityWrapper;
import es.jcyl.ita.formic.repo.Entity;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class JexlFormUtils {
    //protected static final Map<String, Object> funcs = new HashMap<String, Object>().put("math", Math.class);
    protected static final Map<String, Object> funcs;

    static {
        Map<String, Object> aMap = new HashMap<String, Object>();
        aMap.put("math", Math.class);
        funcs = Collections.unmodifiableMap(aMap);
    }

    protected static final JexlEngine jexl = new JexlBuilder().cache(256).namespaces(funcs)
            .strict(false).silent(false).create();

    protected static final JxltEngine jxltEngine = new TemplateEngine((Engine) jexl, false,
            256, '$', '#');

    public static Object eval(Context ctx, String expression) {
        try {
            JxltEngine.Expression exl = jxltEngine.createExpression(expression);
            return exl.evaluate(new JexlContextWrapper(ctx));
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "An error occurred while trying to evaluate the jexl expression [%s].",
                    expression
            ), e);
        }
    }

    public static Object eval(JexlContext ctx, String expression) {
        try {
            JxltEngine.Expression exl = jxltEngine.createExpression(expression);
            return exl.evaluate(ctx);
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "An error occurred while trying to evaluate the jexl expression [%s].",
                    expression
            ), e);
        }
    }

    public static Object eval(Entity entity, String expression) {
        try {
            JxltEngine.Expression exl = jxltEngine.createExpression(expression);
            JexlContext jc = new MapContext();
            jc.set("entity", new JexlEntityWrapper(entity));
            return exl.evaluate(jc);
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "An error occurred while trying to evaluate the jexl expression [%s] on entity[%s].",
                    expression, entity.toString()
            ), e);
        }
    }

    public static Object eval(ComponentContext ctx, String expression) {
        // First try on entity
        Object value = eval(ctx.getEntity(), expression);
        // then try on form fields
        if (value != null) {
            return value;
        }
        return eval(ctx, expression);
    }

    public static Object eval(Context ctx, ValueBindingExpression valueExpression) {
        return valueExpression.getExpression().evaluate(new JexlContextWrapper(ctx));
    }

    public static Object eval(Entity entity, ValueBindingExpression valueExpression) {
        JexlContext jc = new MapContext();
        jc.set("entity", new JexlEntityWrapper(entity));
        return valueExpression.getExpression().evaluate(jc);
    }

    public static Object[] bulkEval(Entity entity, UIComponent[] components) {
        JexlContext jc = new MapContext();
        jc.set("entity", new JexlEntityWrapper(entity));

        Object[] values = new Object[components.length];
        for (int i = 0; i < components.length; i++) {
            try {
                values[i] = components[i].getValueExpression().getExpression().evaluate(jc);
            } catch (Exception e) {
                throw new RuntimeException(String.format(
                        "An error occurred while trying to evaluate the jexl expression [%s] on entity[%s].",
                        components[i].getValueExpression().getExpression(), entity.toString()
                ), e);
            }
        }
        return values;
    }

    public static JxltEngine.Expression createExpression(String expression) {
        return jxltEngine.createExpression(expression);
    }

}
