package es.jcyl.ita.frmdrd.repo.query;
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


import es.jcyl.ita.crtrepo.query.Condition;
import es.jcyl.ita.crtrepo.query.Operator;
import es.jcyl.ita.frmdrd.el.ValueBindingExpression;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Extends Condition class to add ValueBinding expression used to bind the condition value
 * to a context expression.
 */
public class ConditionBinding extends Condition {

    private ValueBindingExpression bindingExpression;

    public ConditionBinding(String property, Operator op, Object value) {
        super(property, op, value);
    }

    public ConditionBinding(String property, Operator op, Object[] values) {
        super(property, op, values);
    }

    public ValueBindingExpression getBindingExpression() {
        return bindingExpression;
    }

    public void setBindingExpression(ValueBindingExpression bindingExpression) {
        this.bindingExpression = bindingExpression;
    }

    public static ConditionBinding cond(Condition cond, ValueBindingExpression expr) {
        ConditionBinding c;
        if (cond.getValue() != null) {
            c = new ConditionBinding(cond.getProperty(), cond.getOp(), cond.getValue());
        } else {
            c = new ConditionBinding(cond.getProperty(), cond.getOp(), cond.getValues());
        }
        c.setBindingExpression(expr);
        return c;
    }
}
