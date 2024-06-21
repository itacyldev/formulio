package es.jcyl.ita.formic.forms.repo.filter;
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

import java.util.HashSet;
import java.util.Set;

import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.repo.query.ConditionBinding;
import es.jcyl.ita.formic.repo.query.Criteria;
import es.jcyl.ita.formic.repo.query.Expression;
import es.jcyl.ita.formic.repo.query.Predicate;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class PredicateBindingExprCollector implements BindingExpressionCollector<Predicate> {

    @Override
    public Set<ValueBindingExpression> collect(Predicate expression) {
        Set<ValueBindingExpression> set = new HashSet();

        if (expression instanceof Criteria) {
            for (Expression kid : ((Criteria) expression).getChildren()) {
                set.addAll(collect((Predicate) kid));
            }
        } else if (expression instanceof ConditionBinding) {
            set.add(((ConditionBinding) expression).getBindingExpression());
        }
        return set;
    }
}
