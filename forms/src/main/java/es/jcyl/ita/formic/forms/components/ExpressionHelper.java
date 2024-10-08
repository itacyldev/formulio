package es.jcyl.ita.formic.forms.components;
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.repo.filter.PredicateBindingExprCollector;
import es.jcyl.ita.formic.forms.repo.query.ConditionBinding;
import es.jcyl.ita.formic.repo.query.Criteria;
import es.jcyl.ita.formic.repo.query.Expression;
import es.jcyl.ita.formic.repo.query.Filter;
import es.jcyl.ita.formic.repo.query.Predicate;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ExpressionHelper {


    public static Set<ValueBindingExpression> getExpressions(UIComponent component) {
        Set<ValueBindingExpression> express = new HashSet<>();
        if (component.getValueExpression() != null) {
            express.add(component.getValueExpression());
        }
        if (component.getRenderExpression() != null) {
            express.add(component.getRenderExpression());
        }
        return express;
    }

    public static Set<ValueBindingExpression> getExpressions(FilterableComponent component) {
        Set<ValueBindingExpression> expressions = getExpressions((UIComponent) component);
        Filter filter = component.getFilter();

        // add all the expressions included in the filter definition
        Set<ValueBindingExpression> filterExp = new HashSet<>();
        if (filter != null) {
            findExpressions(filter.getExpression(), filterExp);
        }
        expressions.addAll(filterExp);
        return expressions;
    }

    private static void findExpressions(Expression expr, Set<ValueBindingExpression> lstExpressions) {
        if (expr == null) {
            return;
        }
        if (expr instanceof ConditionBinding) {
            lstExpressions.add(((ConditionBinding) expr).getBindingExpression());
        } else {
            if (expr instanceof Criteria) {
                // do deeper
                Criteria criteria = (Criteria) expr;
                if (criteria.getChildren() != null) {
                    for (Expression kid : criteria.getChildren()) {
                        findExpressions(kid, lstExpressions);
                    }
                }
            }
        }
    }

    /**
     * Looks for binding expressions in the passed expression
     *
     * @param expression
     * @return
     */
    public static Collection<? extends ValueBindingExpression> getExpressions(Expression expression) {
        Set<ValueBindingExpression> bindingExpressions = new HashSet<>();
        // TODO: create factory and return a proper collector for each Expression type, right now
        //  threre's just one Expression with ValueBindingExpressions
        if (expression instanceof Predicate) {
            PredicateBindingExprCollector collector = new PredicateBindingExprCollector();
            return collector.collect((Predicate) expression);

        }
        return bindingExpressions;
    }
}
