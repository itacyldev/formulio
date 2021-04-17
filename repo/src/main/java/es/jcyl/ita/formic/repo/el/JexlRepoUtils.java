package es.jcyl.ita.formic.repo.el;
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

import java.util.List;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.el.wrappers.JexlContextWrapper;
import es.jcyl.ita.formic.repo.el.wrappers.JexlEntityWrapper;
import es.jcyl.ita.formic.repo.query.JexlEntityExpression;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class JexlRepoUtils {

    protected static final JexlEngine jexl = new JexlBuilder().cache(256)
            .strict(false).silent(false).create();

    protected static final JxltEngine jxltEngine = new TemplateEngine((Engine) jexl, true,
            256, '$', '#');

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

    public static Object eval(Context context, String expression) {
        JxltEngine.Expression exl = jxltEngine.createExpression(expression);
        return eval(context, exl);
    }

    public static Object eval(Context context, JxltEngine.Expression exl) {
        JexlContextWrapper jc = new JexlContextWrapper(context);
        return exl.evaluate(jc);
    }

    public static Object eval(Entity entity, String expression) {
        JxltEngine.Expression exl = null;
        try {
            exl = jxltEngine.createExpression(expression);
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "An error occurred while trying to create JEXL expression [%s] for entity[%s].",
                    expression, entity.toString()
            ), e);
        }
        return eval(entity, exl);
    }

    public static Object[] bulkEval(List<Entity> entityList, String expression) {
        JxltEngine.Expression exl = jxltEngine.createExpression(expression);
        return bulkEval(entityList, exl);
    }

    public static Object[] bulkEval(List<Entity> entityList, JexlEntityExpression entityExpression) {
        return bulkEval(entityList, entityExpression.getExpression());
    }

    public static Object[] bulkEval(List<Entity> entityList, JxltEngine.Expression exl) {
        JexlContext jc = new MapContext();

        Object[] values = new Object[entityList.size()];
        int i = 0;
        for (Entity entity : entityList) {
            jc.set("entity", new JexlEntityWrapper(entity));
            try {
                values[i] = exl.evaluate(jc);
            } catch (Exception e) {
                throw new RuntimeException(String.format(
                        "An error occurred while trying to evaluate the jexl expression [%s] on entity[%s].",
                        exl.asString(), entity), e);
            }
            i++;
        }
        return values;
    }

    public static Object eval(Entity entity, JxltEngine.Expression exl) {
        try {
            JexlContext jc = new MapContext();
            jc.set("entity", new JexlEntityWrapper(entity));
            return exl.evaluate(jc);
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "An error occurred while trying to evaluate the jexl expression [%s] on entity[%s].",
                    exl, entity.toString()
            ), e);
        }
    }

    public static JxltEngine.Expression createExpression(String expression) {
        return jxltEngine.createExpression(expression);
    }


}
