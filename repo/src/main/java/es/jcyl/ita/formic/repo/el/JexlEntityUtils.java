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

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JxltEngine;
import org.apache.commons.jexl3.MapContext;

import java.util.List;

import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.el.wrappers.JexlEntityWrapper;
import es.jcyl.ita.formic.repo.query.JexlEntityExpression;
import es.jcyl.ita.formic.core.jexl.JexlUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class JexlEntityUtils extends JexlUtils {

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

}
