package es.jcyl.ita.formic.forms.repo;

import org.junit.Assert;
import org.junit.Test;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.repo.query.Condition;
import es.jcyl.ita.formic.repo.query.Criteria;
import es.jcyl.ita.formic.repo.query.Expression;
import es.jcyl.ita.formic.repo.query.Operator;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.forms.repo.query.ConditionBinding;
import es.jcyl.ita.formic.forms.repo.query.CriteriaVisitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class CriteriaVisitorTest {

    CriteriaVisitor visitor = new CriteriaVisitor();
    ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();

    @Test
    public void testCreateVisitor() {
        Context context = new BasicContext("tt");
        context.put("f1", 1);
        context.put("f2", 2);
        context.put("f3", 3);
        context.put("f4", 4);

        // create criteria definition to use as template
        Fixture[] fixtures = new Fixture[]{
                new Fixture(simpleCriteria("f1"), context("f1"), 1),
                new Fixture(simpleCriteria("f1"), context(), 0), // value for f1 is not set
                new Fixture(andCriteria(), context("f1", "f2", "f3"), 3), // all values set
                new Fixture(andCriteria(), context("f1", "f3"), 2), // two values set, one missing
                new Fixture(andCriteria(), context("f1"), 1), // one value set two missing
                new Fixture(andCriteria(), context(), 0), // no values in the context, no filtering
                new Fixture(andNestedOrCriteria(), context("f1", "f2", "f3", "f4"), 4),
                new Fixture(andNestedOrCriteria(), context("f1", "f2", "f3"), 3),
                new Fixture(andNestedOrCriteria(), context("f1", "f2"), 2),
                new Fixture(andNestedOrCriteria(), context("f1"), 1),
                new Fixture(andNestedOrCriteria(), context(), 0), // no values in the context, no filtering
        };
        for (Fixture fixture : fixtures) {
            Criteria effCriteria = visitor.visit(fixture.criteria, fixture.context);
            int actual = assertTree(effCriteria);
            if (actual == 0) {
                assertNull(effCriteria);
            }
            assertEquals(fixture.expected, actual);
            // check all nodes have values

        }
    }

    public class Fixture {
        public Criteria criteria;
        public Context context;
        public int expected;

        public Fixture(Criteria criteria, Context context, int numExpected) {
            this.expected = numExpected;
            this.criteria = criteria;
            this.context = context;
        }
    }

    private Criteria simpleCriteria(String field) {
        ConditionBinding condition = new ConditionBinding(field, Operator.EQ, null);
        condition.setBindingExpression(exprFactory.create("${f1}"));
        return Criteria.and(condition);
    }

    private Criteria andCriteria() {
        ConditionBinding c1 = new ConditionBinding("f1", Operator.EQ, null);
        c1.setBindingExpression(exprFactory.create("${f1}"));
        ConditionBinding c2 = new ConditionBinding("f2", Operator.EQ, null);
        c2.setBindingExpression(exprFactory.create("${f2}"));
        ConditionBinding c3 = new ConditionBinding("f2", Operator.EQ, null);
        c3.setBindingExpression(exprFactory.create("${f3}"));
        return Criteria.and(c1, c2, c3);
    }

    private Criteria andNestedOrCriteria() {
        ConditionBinding c1 = new ConditionBinding("f1", Operator.EQ, null);
        c1.setBindingExpression(exprFactory.create("${f1}"));
        ConditionBinding c2 = new ConditionBinding("f2", Operator.EQ, null);
        c2.setBindingExpression(exprFactory.create("${f2}"));
        ConditionBinding c3 = new ConditionBinding("f3", Operator.EQ, null);
        c3.setBindingExpression(exprFactory.create("${f3}"));
        ConditionBinding c4 = new ConditionBinding("f4", Operator.EQ, null);
        c4.setBindingExpression(exprFactory.create("${f4}"));
        return Criteria.and(c1, c2, Criteria.or(c3, c4));
    }

    /**
     * Creates a context with the given variable names and random values for them.
     *
     * @param values
     * @return
     */
    private Context context(String... values) {
        Context ctx = new BasicContext("test");
        if (values != null) {
            for (String value : values) {
                ctx.put(value, RandomUtils.randomInt(0, 1000));
            }
        }
        return ctx;
    }

    /**
     * Goes over all the tree nodes look for empty nodes that should've been pruned and counts the
     * number of terminal nodes (Conditions). It also all the Conditions has a value.
     *
     * @param expr
     * @return
     */
    private int assertTree(Expression expr) {
        if (expr == null) {
            return 0;
        }
        int child = 0;
        if (expr instanceof Criteria) {
            Criteria criteria = (Criteria) expr;
            if (criteria.getChildren() == null || criteria.getChildren().length == 0) {
                // there's and empty group node in the tree, fail
                Assert.fail("Found and empty criteria in the expression: " + expr.toString());
            }
            for (Expression kid : criteria.getChildren()) {
                child += assertTree(kid);
            }
            return child;
        } else {
            if (((Condition) expr).getValue() == null) {
                Assert.fail("Found a null node in the expression: " + expr.toString());
            }
            return 1;
        }
    }
}