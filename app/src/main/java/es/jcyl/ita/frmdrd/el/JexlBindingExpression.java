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

import androidx.annotation.NonNull;

import org.apache.commons.jexl3.JxltEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Component expression used to calculate the value of the expression or the binding relation
 * between this component and an entity property.
 */
public class JexlBindingExpression implements ValueBindingExpression {

    private static final String IMMEDIATE_EXPRESSION = "org.apache.commons.jexl3.internal.TemplateEngine$ImmediateExpression";
    private static final String COMPOSITE_EXPRESSION = "org.apache.commons.jexl3.internal.TemplateEngine$CompositeExpression";

    private List<String> vars;
    private final JxltEngine.Expression expression;
    private Class expectedType;
    private boolean isLiteral;
    private boolean isReadOnly;
    private Object calculatedValue;

    public JexlBindingExpression(JxltEngine.Expression expression) {
        this(expression, null);
    }

    public JexlBindingExpression(JxltEngine.Expression expression, Class expectedType) {
        this.expression = expression;
        this.expectedType = expectedType;
        setIsLiteral();
        setIsReadonly();
    }

    /**
     * Returns true if the expression doesn't depend on any variable
     *
     * @return
     */
    public boolean isLiteral() {
        return isLiteral;
    }

    /**
     * Returns true if the expression cannot be used to update and entity property.
     * Just if the expression refers to one entity property it can be used to update
     * the entity's value.
     *
     * @return
     */
    public boolean isReadOnly() {
        return isReadOnly;

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

    private void setIsReadonly() {
        if (this.isLiteral) {
            this.isReadOnly = true;
            return;
        }
        // immediate expressions define a direct reference to a bean method
        String className = this.expression.getClass().getName();
        if (!className.equals(IMMEDIATE_EXPRESSION) && !className.equals(COMPOSITE_EXPRESSION)) {
            this.isReadOnly = true;
            return;
        } else {
            // there's just one variable in the expression and it depends on one of the
            // entity properties without submethods call on the property
            // TODO:
            List<String> vars = getDependingVariables();
            this.isReadOnly = vars.size() > 1;
            if (!isReadOnly) {
                // check the property is not used to access a method ex: entity.property.method()
                String accessedProperty = vars.get(0).toLowerCase();
                this.isReadOnly = !accessedProperty.contains("entity")
                        || hasNestedMethodCall(this.expression.asString(), accessedProperty);
            }
        }
    }

    /**
     * Checks if the referenced property is used in the expression to call an additional sub-method
     *
     * @param expression
     * @param accessedProperty
     * @return
     */
    private boolean hasNestedMethodCall(String expression, String accessedProperty) {
        // TODO: JEXL is hermetic at this point and we cant access AST nodes to check if threre's a method
        // reference below the property AST node
        int pos = expression.indexOf(accessedProperty);
        // check if after the property is something different that " " or "}", it that case the expression
        // is using a method of the property Object
        char afterExpressionChar = expression.charAt(pos + accessedProperty.length());
        return afterExpressionChar != ' ' && afterExpressionChar != '}';
    }

    private void setIsLiteral() {
        this.isLiteral = (getDependingVariables() == null || getDependingVariables().size() == 0);
    }

    @NonNull
    @Override
    public String toString() {
        return expression.getSource().toString();
    }
}
