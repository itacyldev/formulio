package es.jcyl.ita.frmdrd.el;
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

import org.apache.commons.jexl3.JxltEngine;
import org.mini2Dx.beanutils.ConvertUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ValueExpressionFactory {

    public ValueBindingExpression create(String expression) {
        JxltEngine.Expression jexlExpr = JexlUtils.createExpression(expression);
        if (isLiteralExpression(jexlExpr)) {
            return new LiteralBindingExpression(expression);
        } else {
            return new JexlBindingExpression(jexlExpr);
        }
    }

    public ValueBindingExpression create(String expression, Class type) {
        JxltEngine.Expression jexlExpr = JexlUtils.createExpression(expression);
        if (isLiteralExpression(jexlExpr)) {
            return new LiteralBindingExpression(ConvertUtils.convert(expression, type));
        } else {
            return new JexlBindingExpression(jexlExpr, type);
        }
    }

    public static boolean isLiteralExpression(JxltEngine.Expression expression) {
        return (expression.getVariables() == null || expression.getVariables().size() == 0);
    }
}
