package es.jcyl.ita.frmdrd.el;
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

import org.apache.commons.jexl3.JxltEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Component expression used to calculate the value of the expression or the binding relation
 * between this component and an entity property.
 */
public class ValueBindingExpression {

    private static final String IMMEDIATE_EXPRESSION = "org.apache.commons.jexl3.internal.TemplateEngine$ImmediateExpression";
    private List<String> vars;
    private final JxltEngine.Expression expression;
    private Class expectedType;

    public ValueBindingExpression(JxltEngine.Expression expression) {
        this.expression = expression;
    }

    /**
     * Returns true if the expression doesn't depend on any variable
     *
     * @return
     */
    public boolean isLiteral() {
        return (getDependingVariables() == null || getDependingVariables().size() == 0);
    }

    /**
     * Returns true if the expression cannot be used to update and entity property.
     * Just if the expression refers to one entity property it can be used to update
     * the entity's value.
     *
     * @return
     */
    public boolean isReadOnly() {
        // immediate expressions define a direct reference to a bean method
        return !(this.expression.getClass().getName().equals(IMMEDIATE_EXPRESSION));
//
//        if (getDependingVariables() == null || getDependingVariables().size() != 1) {
//            return true;
//        } else {
//            // there's just one variable in the expression and it depends on one of the
//            // entity's properties
//            return !getDependingVariables().get(0).contains("entity.");
//        }
    }

    public String getBindingProperty() {
        if (isReadOnly()) {
            throw new IllegalStateException(String.format("The expression defined in field is readonly and " +
                    "cannot be used as binding with an entity property." +
                    " The expression [%s] must contain exactly one entity property.", this.expression));
        }
        return this.getDependingVariables().get(0);
    }

    /**
     * Returns the list of variables
     *
     * @return
     */
    public List<String> getDependingVariables() {
        if (this.vars == null) {
            this.vars = new ArrayList<>();
            for (List<String> lstVars : this.expression.getVariables()) {
                StringBuffer stb = new StringBuffer();
                for (String part : lstVars) {
                    stb.append(part + ".");
                }
                vars.add(stb.toString().substring(0, stb.length() - 1));
            }
        }
        return vars;
    }

    public JxltEngine.Expression getExpression() {
        return expression;
    }

    public Class getExpectedType() {
        return (this.expectedType == null) ? Object.class : this.expectedType;
    }

    public void setExpectedType(Class expectedType) {
        this.expectedType = expectedType;
    }
}
