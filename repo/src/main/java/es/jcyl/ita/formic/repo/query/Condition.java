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

public class Condition extends Predicate {

    String property;
    Operator op;
    Object value;
    Object[] values;

    public Condition(String property, Operator op, Object value) {
        this.property = property;
        this.op = op;
        this.value = value;
        this.values = null;
    }

    public Condition(String property, Operator op, Object[] values) {
        this.property = property;
        this.op = op;
        this.values = values;
    }

    public Condition(Condition cond) {
        this.property = cond.property;
        this.op = cond.op;
        this.value = cond.value;
        this.values = cond.values;
    }

    public String getProperty() {
        return property;
    }

    public Operator getOp() {
        return op;
    }

    public Object getValue() {
        return value;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    public static Condition eq(String prop, Object value) {
        return new Condition(prop, Operator.EQ, value);
    }

    public static Condition lt(String prop, Object value) {
        return new Condition(prop, Operator.LT, value);
    }

    public static Condition le(String prop, Object value) {
        return new Condition(prop, Operator.LE, value);
    }

    public static Condition gt(String prop, Object value) {
        return new Condition(prop, Operator.GT, value);
    }

    public static Condition ge(String prop, Object value) {
        return new Condition(prop, Operator.GE, value);
    }

    public static Condition in(String prop, Object[] values) {
        return new Condition(prop, Operator.IN, values);
    }

    public static Condition in(String prop, Object value) {
        return new Condition(prop, Operator.IN, new Object[]{value});
    }

    public static Condition notIn(String prop, Object[] values) {
        return new Condition(prop, Operator.NOT_IN, values);
    }

    public static Condition notIn(String prop, Object value) {
        return new Condition(prop, Operator.NOT_IN, new Object[]{value});
    }

    public static Condition contains(String prop, Object value) {
        return new Condition(prop, Operator.CONTAINS, value);
    }

    public static Condition endsWith(String prop, Object value) {
        return new Condition(prop, Operator.ENDS_WITH, value);
    }

    public static Condition startsWith(String prop, Object value) {
        return new Condition(prop, Operator.STARTS_WITH, value);
    }

    public static Condition isNotNull(String prop, Object value) {
        return new Condition(prop, Operator.NOT_NULL, value);
    }

    public static Condition isNull(String prop, Object value) {
        return new Condition(prop, Operator.IS_NULL, value);
    }

    @Override
    public String toString() {
        return String.format("%s_%s_%s", this.property, this.op, (this.value != null) ? value : values);
    }
}
