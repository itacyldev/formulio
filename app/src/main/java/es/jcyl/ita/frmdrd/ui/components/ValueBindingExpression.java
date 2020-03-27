package es.jcyl.ita.frmdrd.ui.components;
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

import java.util.List;

/**
 * Component expression used to calculate the value of the expression or the binding relation
 * between this component and an entity property.
 */
public class ValueBindingExpression {

    private List<String> vars;
    private final JxltEngine.Expression expression;

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
     * Returns true if the expression refers just to one entity field, so the binding between ui
     * component and entity value can be bidirectional.
     *
     * @return
     */
    public boolean isEntityFieldBinding() {
        if (getDependingVariables() == null || getDependingVariables().size() != 1) {
            return false;
        } else {
            // there's just one variable in the expression and it depends on one of the
            // entity's properties
            return getDependingVariables().get(0).contains("entity.");
        }
    }

    /**
     * Returns the list of variables
     *
     * @return
     */
    public List<String> getDependingVariables() {
        return vars;
    }

    public JxltEngine.Expression getExpression() {
        return expression;
    }
}
