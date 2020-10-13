package es.jcyl.ita.formic.repo.el;
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

import androidx.annotation.NonNull;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JxltEngine;
import org.mini2Dx.beanutils.ConvertUtils;

import java.util.List;
import java.util.Set;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class LiteralBindingExpression implements ValueBindingExpression, JxltEngine.Expression {
    private final Object value;
    private final String stringValue;

    public LiteralBindingExpression(Object value) {
        this.value = value;
        this.stringValue = ConvertUtils.convert(value);
    }

    @Override
    public boolean isLiteral() {
        return true;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public String getBindingProperty() {
        return null;
    }

    @Override
    public List<String> getDependingVariables() {
        return null;
    }

    @Override
    public JxltEngine.Expression getExpression() {
        return this;
    }

    @Override
    public Class getExpectedType() {
        return value.getClass();
    }

    @Override
    public void setExpectedType(Class expectedType) {
        throw new UnsupportedOperationException("Not applicable for literal expressions.");
    }

    @Override
    public String asString() {
        return stringValue;
    }

    @Override
    public StringBuilder asString(StringBuilder strb) {
        return null;
    }

    @Override
    public Object evaluate(JexlContext context) {
        return value;
    }

    @Override
    public JxltEngine.Expression getSource() {
        return this;
    }

    @Override
    public Set<List<String>> getVariables() {
        return null;
    }

    @Override
    public boolean isDeferred() {
        return false;
    }

    @Override
    public boolean isImmediate() {
        return true;
    }

    @Override
    public JxltEngine.Expression prepare(JexlContext context) {
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return stringValue;
    }
}
