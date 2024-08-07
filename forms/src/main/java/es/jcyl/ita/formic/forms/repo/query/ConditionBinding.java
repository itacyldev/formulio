package es.jcyl.ita.formic.forms.repo.query;
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


import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.repo.query.Condition;
import es.jcyl.ita.formic.repo.query.Operator;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Extends Condition class to add ValueBinding expression used to link the condition value
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
        ConditionBinding bCond;
        if (cond.getValue() != null) {
            bCond = new ConditionBinding(cond.getProperty(), cond.getOp(), cond.getValue());
        } else {
            bCond = new ConditionBinding(cond.getProperty(), cond.getOp(), cond.getValues());
        }
        bCond.setBindingExpression(expr);
        return bCond;
    }

    public static ConditionBinding cond(String property, Operator op, ValueBindingExpression expr) {
        ConditionBinding cond = new ConditionBinding(property, op, null);
        cond.setBindingExpression(expr);
        return cond;
    }

    @Override
    public String toString() {
        return String.format("%s_%s_%s", getProperty(), getOp(), getBindingExpression());
    }
}
