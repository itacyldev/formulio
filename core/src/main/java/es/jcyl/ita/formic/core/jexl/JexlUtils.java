package es.jcyl.ita.formic.core.jexl;
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
import org.apache.commons.jexl3.internal.Engine;
import org.apache.commons.jexl3.internal.TemplateEngine;
import org.mini2Dx.beanutils.ConvertUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.jexl.JexlContextWrapper;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class JexlUtils {
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

    public static <T> T eval(Context ctx, String expression, Class<T> clazz) {
        try {
            JxltEngine.Expression exl = jxltEngine.createExpression(expression);
            Object eval =  exl.evaluate(new JexlContextWrapper(ctx));
            return (T) ConvertUtils.convert(eval, clazz);
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

    public static Object eval(Context context, JxltEngine.Expression exl) {
        JexlContextWrapper jc = new JexlContextWrapper(context);
        return exl.evaluate(jc);
    }

    public static JxltEngine.Expression createExpression(String expression) {
        return jxltEngine.createExpression(expression);
    }

}

