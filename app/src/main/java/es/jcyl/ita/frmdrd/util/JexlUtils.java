package es.jcyl.ita.frmdrd.util;
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
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.frmdrd.context.Context;
import es.jcyl.ita.frmdrd.context.impl.FormContext;
import es.jcyl.ita.frmdrd.context.wrappers.JexlContextWrapper;
import es.jcyl.ita.frmdrd.context.wrappers.JexlEntityWrapper;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class JexlUtils {
    protected static final JexlEngine jexl = new JexlBuilder().cache(512)
            .strict(true).silent(false).create();

    public static Object eval(Context ctx, String expression) {
        JexlExpression exl = jexl.createExpression(expression);
        try {
            return exl.evaluate(new JexlContextWrapper(ctx));
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "An error ocurred while trying to evaluate the jexl expression [%s].",
                    expression
            ), e);
        }
    }

    public static Object eval(Entity entity, String expression) {
        JexlExpression exl = jexl.createExpression(expression);
        try {
            JexlContext jc = new MapContext();
            jc.set("entity", new JexlEntityWrapper(entity));
            return exl.evaluate(jc);
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "An error ocurred while trying to evaluate the jexl expression [%s] on entity[%s].",
                    expression, entity.toString()
            ), e);
        }
    }

    public static Object eval(FormContext ctx, String expression) {
        // First try on entity
        Object value = eval(ctx.getEntity(), expression);
        // then try on form fields
        if (value != null) {
            return value;
        }
        return eval(ctx, expression);
    }

}
