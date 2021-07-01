package es.jcyl.ita.formic.repo.db;
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

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.RepositoryException;
import es.jcyl.ita.formic.repo.db.query.RawWhereCondition;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.EntityDao;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.PropertyConditionHelper;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.query.Condition;
import es.jcyl.ita.formic.repo.query.Criteria;
import es.jcyl.ita.formic.repo.query.Expression;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Flyweight interpreter to create greendao QueryBuilder from a criteria expression
 * </p>
 */
public class ExpressionInterpreter {

    private static final SingleExpression SINGLE_EXPRESSION = new SingleExpression();
    private static final GroupExpression AND_EXPRESSION = new GroupExpression("and");
    private static final GroupExpression OR_EXPRESSION = new GroupExpression("or");
    private static final RawWhereExpression WHERE_EXPRESSION = new RawWhereExpression();


    public WhereCondition interpret(EntityDao dao, QueryBuilder<Entity> qb, Expression e) {
        return expression(e).interpret(dao, qb, e);
    }

    static abstract class AbstractInterExpression<E extends Expression> {
        public abstract WhereCondition interpret(EntityDao dao, QueryBuilder<Entity> qBuilder, E e);
    }

    static class SingleExpression extends AbstractInterExpression<Condition> {
        @Override
        public WhereCondition interpret(EntityDao dao, QueryBuilder<Entity> qBuilder, Condition e) {
            Property property = dao.getPropertyByName(e.getProperty());
            if (property == null) {
                EntityMeta meta = dao.entityConfig().getMeta();
                throw new RepositoryException(String.format("Error while trying to interpret repoFilter expression. " +
                                "Invalid property name: [%s], in doesn't exist in repo [%s].", e.getProperty(),
                        meta.getName()));
            }
            return PropertyConditionHelper.create(e, property);
        }
    }

    static class RawWhereExpression extends AbstractInterExpression<RawWhereCondition> {
        @Override
        public WhereCondition interpret(EntityDao dao, QueryBuilder<Entity> qBuilder, RawWhereCondition e) {
            return e.getWhereCondition();
        }
    }

    static class GroupExpression extends AbstractInterExpression<Criteria> {
        private final String op;

        public GroupExpression(String op) {
            this.op = op;
        }

        @Override
        public WhereCondition interpret(EntityDao dao, QueryBuilder<Entity> qBuilder, Criteria e) {
            Expression[] children = e.getChildren();
            if (children.length == 1) {
                return expression(children[0]).interpret(dao, qBuilder, children[0]);
            }
            WhereCondition firstCond = expression(children[0]).interpret(dao, qBuilder, children[0]);
            WhereCondition secondCond = expression(children[1]).interpret(dao, qBuilder, children[1]);
            if (children.length == 2) {
                return op.equals("and") ? qBuilder.and(firstCond, secondCond)
                        : qBuilder.or(firstCond, secondCond);
            } else {
                WhereCondition[] restConds = new WhereCondition[children.length - 2];
                for (int i = 0; i < e.getChildren().length - 2; i++) {
                    restConds[i] = expression(children[i + 2]).interpret(dao, qBuilder, children[i + 2]);
                }
                return op.equals("and") ? qBuilder.and(firstCond, secondCond, restConds)
                        : qBuilder.or(firstCond, secondCond, restConds);
            }
        }
    }

    private static AbstractInterExpression expression(Expression e) {
        if (e instanceof Criteria) {
            if (((Criteria) e).getType() == Criteria.CriteriaType.AND) {
                return AND_EXPRESSION;
            } else {
                return OR_EXPRESSION;
            }
        } else if (e instanceof RawWhereCondition) {
            return WHERE_EXPRESSION;
        } else {
            return SINGLE_EXPRESSION;
        }
    }
}
