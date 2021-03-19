package es.jcyl.ita.formic.forms.repo.filter;
/*
 *
 *  * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import org.junit.Test;

import java.util.Set;

import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.forms.repo.query.ConditionBinding;
import es.jcyl.ita.formic.forms.repo.query.CriteriaVisitor;
import es.jcyl.ita.formic.repo.query.Criteria;
import es.jcyl.ita.formic.repo.query.Operator;

import static org.junit.Assert.assertTrue;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class PredicateBindingExprCollectorTest {

    CriteriaVisitor visitor = new CriteriaVisitor();
    ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();

    @Test
    public void basic() {
        ConditionBinding c1 = new ConditionBinding("f1", Operator.EQ, null);
        c1.setBindingExpression(exprFactory.create("${f1}"));
        ConditionBinding c2 = new ConditionBinding("f2", Operator.EQ, null);
        c2.setBindingExpression(exprFactory.create("${f2}"));
        ConditionBinding c3 = new ConditionBinding("f3", Operator.EQ, null);
        c3.setBindingExpression(exprFactory.create("${f3}"));
        ConditionBinding c4 = new ConditionBinding("f4", Operator.EQ, null);
        c4.setBindingExpression(exprFactory.create("${f4}"));

        Criteria expression = Criteria.and(c1, c2, Criteria.or(c3, c4));

        PredicateBindingExprCollector collector = new PredicateBindingExprCollector();
        Set<ValueBindingExpression> set = collector.collect(expression);

        assertTrue(set.size() == 4);


    }
}
