package es.jcyl.ita.formic.forms.components.column;

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

import es.jcyl.ita.crtrepo.query.Operator;
import es.jcyl.ita.formic.repo.el.ValueBindingExpression;
/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */
public class UIColumnFilter {

    private final Operator DEFAULT_OPERATOR = Operator.CONTAINS;

    private String filterProperty;
    private ValueBindingExpression filterValueExpression;
    private Operator matchingOperator = DEFAULT_OPERATOR;
    private String orderProperty;

    public String getFilterProperty() {
        return filterProperty;
    }

    public void setFilterProperty(String filterProperty) {
        this.filterProperty = filterProperty;
    }

    public ValueBindingExpression getFilterValueExpression() {
        return filterValueExpression;
    }

    public void setFilterValueExpression(ValueBindingExpression filterValueExpression) {
        this.filterValueExpression = filterValueExpression;
    }

    public Operator getMatchingOperator() {
        return matchingOperator;
    }

    public void setMatchingOperator(Operator matchingOperator) {
        this.matchingOperator = matchingOperator;
    }

    public String getOrderProperty() {
        return orderProperty;
    }

    public void setOrderProperty(String orderProperty) {
        this.orderProperty = orderProperty;
    }
}