package es.jcyl.ita.formic.repo.query;
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


import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.context.Context;
import es.jcyl.ita.crtrepo.query.Condition;
import es.jcyl.ita.crtrepo.query.Criteria;
import es.jcyl.ita.crtrepo.query.Expression;
import es.jcyl.ita.formic.repo.el.JexlUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 *
 * Evaluates the criteria conditions using context information.
 */
public class CriteriaVisitor {
    private static final CriteriaElement CRITERIA_ELEMENT = new CriteriaElement();
    private static final ConditionElement CONDITION_ELEMENT = new ConditionElement();
    private static final ConditionBindingElement CONDITION_BINDING_ELEMENT = new ConditionBindingElement();

    /**
     * Goes across a Criteria tree evaluating the terminal conditions, when a jexl expresion
     * is defined in a ConditionBinding, it uses the context to extract the actual value to apply
     * in the filter.
     * If the variable bound to a ConditionBinding is empty in the context, the condition is
     * turn down, so at the end of the walk, an effective Criteria is obtained just with the
     * conditions with variables defined in the context.
     *  to obta
     * @param criteriaDef
     * @param context
     * @return
     */
    public Criteria visit(Criteria criteriaDef, Context context) {
        return (Criteria) element(criteriaDef).accept(criteriaDef, context);
    }

    public interface Element<T extends Expression> {
        Expression accept(T e, Context context);
    }

    public static class ConditionBindingElement implements Element<ConditionBinding> {

        @Override
        public Expression accept(ConditionBinding expre, Context context) {
            // if the element has value, add it to parent
            Object value;
            try {
                value = JexlUtils.eval(context, expre.getBindingExpression());
            } catch (Exception e) {
                // no variable found is treated as null value for this condition
                value = null;
            }
            if (value == null) {
                return null;
            } else {
                Condition c = new Condition(expre);
                if (isArray(value)) {
                    c.setValues(expre.getValues());
                } else {
                    c.setValue(value);
                }
                return c;
            }
        }
    }

    public static class ConditionElement implements Element<Condition> {

        @Override
        public Expression accept(Condition expr, Context context) {
            // condition with fixed value, return original Condition object
            return expr;
        }
    }

    public static boolean isArray(Object obj) {
        return obj != null && obj.getClass().isArray();
    }

    public static class CriteriaElement implements Element<Criteria> {
        @Override
        public Expression accept(Criteria e, Context context) {
            List<Expression> kids = new ArrayList<>();

            for (Expression expr : e.getChildren()) {
                Expression kidExpr = element(expr).accept(expr, context);
                if (kidExpr != null) {
                    kids.add(kidExpr);
                }
            }
            if (kids.size() == 0) {
                // if all the children are null, prune this branch
                return null;
            } else {
                return new Criteria(e.getType(), kids.toArray(new Expression[kids.size()]));
            }
        }
    }

    private static Element element(Expression e) {
        if (e instanceof ConditionBinding) {
            return CONDITION_BINDING_ELEMENT;
        } else if (e instanceof Criteria) {
            return CRITERIA_ELEMENT;
        } else {
            return CONDITION_ELEMENT;
        }
    }

}
