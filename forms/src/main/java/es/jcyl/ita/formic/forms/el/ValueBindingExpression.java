package es.jcyl.ita.formic.forms.el;
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
public interface ValueBindingExpression {


    /**
     * Returns true if the expression doesn't depend on any variable
     *
     * @return
     */
     boolean isLiteral();

    /**
     * Returns true if the expression cannot be used to update and entity property.
     * Just if the expression refers to one entity property it can be used to update
     * the entity's value.
     *
     * @return
     */
     boolean isReadOnly();

     String getBindingProperty();

    /**
     * Returns the list of variables
     *
     * @return
     */
     List<String> getDependingVariables();

     JxltEngine.Expression getExpression();

     Class getExpectedType() ;

     void setExpectedType(Class expectedType);

}
