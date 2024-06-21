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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class Criteria extends Predicate {

    public enum CriteriaType {AND, OR, SINGLE}

    private CriteriaType type;
    private Expression[] children;

    public Criteria(CriteriaType type, Expression[] children) {
        this.type = type;
        this.children = children;
    }

    public Expression[] getChildren() {
        return children;
    }

    public void setChildren(Expression[] childs) {
        this.children = childs;
    }

    public CriteriaType getType() {
        return type;
    }

    public static Criteria and(Expression... expr) {
        if (expr == null || expr.length == 0) {
            throw new IllegalArgumentException("Expressions argument cannot be null.");

        }
        Criteria criteria = new Criteria(CriteriaType.AND, expr);
        return criteria;
    }

    public static Criteria or(Expression... expr) {
        if (expr == null || expr.length == 0) {
            throw new IllegalArgumentException("Expressions argument cannot be null.");

        }
        Criteria criteria = new Criteria(CriteriaType.OR, expr);
        return criteria;
    }

    public static Criteria single(Expression condition) {
        if (condition == null) {
            throw new IllegalArgumentException("Condition argument cannot be null.");

        }
        Criteria criteria = new Criteria(CriteriaType.SINGLE, new Expression[]{condition});
        return criteria;
    }

    @Override
    public String toString() {
        StringBuffer stb = new StringBuffer();
        if (this.getChildren() == null) {
            return "";
        }
        for (Expression exp : this.getChildren()) {
            stb.append(exp.toString());
        }
        return stb.toString();
    }
}
