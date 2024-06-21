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

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.MapContext;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.view.widget.WidgetContext;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.el.JexlEntityUtils;
import es.jcyl.ita.formic.repo.el.wrappers.JexlEntityWrapper;
import es.jcyl.ita.formic.core.jexl.JexlContextWrapper;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class JexlFormUtils extends JexlEntityUtils {


    public static Object eval(WidgetContext ctx, String expression) {
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

}
