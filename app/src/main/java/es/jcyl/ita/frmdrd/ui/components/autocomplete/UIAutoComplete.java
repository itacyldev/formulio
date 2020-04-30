package es.jcyl.ita.frmdrd.ui.components.autocomplete;
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

import java.util.Set;

import es.jcyl.ita.crtrepo.query.Operator;
import es.jcyl.ita.frmdrd.el.ValueBindingExpression;
import es.jcyl.ita.frmdrd.ui.components.ExpressionHelper;
import es.jcyl.ita.frmdrd.ui.components.FilterableComponent;
import es.jcyl.ita.frmdrd.ui.components.select.UISelect;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIAutoComplete extends UISelect {

    private static final String TYPE = "autocomplete";
    private static final String DYN_TYPE = "dynamicAutocomplete";

    /**
     * Expression used to calculate the label of each option when they're obtained
     * from a repo
     */
    private ValueBindingExpression optionLabelExpression;
    /**
     * Entity property used to build a criteria to filter current repository, the filter value
     * will be obtained from current user autocomplete constraint
     */
    private String labelFilteringProperty;
    private Operator valueFilteringOperator = Operator.EQ;
    private String valueProperty;

    private int inputThreshold = 1;

    private boolean forceSelection;


    @Override
    public String getRendererType() {
        return TYPE;
    }

    @Override
    public String getValueConverter() {
        if (!forceSelection) {
            // use textView selection
            return "text";
        }
        if (this.isStatic()) {
            return TYPE;
        } else {
            return DYN_TYPE;
        }
    }

    public ValueBindingExpression getOptionLabelExpression() {
        return optionLabelExpression;
    }

    public void setOptionLabelExpression(ValueBindingExpression optionLabelExpression) {
        this.optionLabelExpression = optionLabelExpression;
    }


    public boolean isForceSelection() {
        return forceSelection;
    }

    public void setForceSelection(boolean forceSelection) {
        this.forceSelection = forceSelection;
    }

    public String getLabelFilteringProperty() {
        return labelFilteringProperty;
    }

    public void setLabelFilteringProperty(String labelFilteringProperty) {
        this.labelFilteringProperty = labelFilteringProperty;
    }

    public String getValueProperty() {
        return valueProperty;
    }

    public void setValueProperty(String valueProperty) {
        this.valueProperty = valueProperty;
    }

    public Operator getValueFilteringOperator() {
        return valueFilteringOperator;
    }

    public void setValueFilteringOperator(Operator valueFilteringOperator) {
        this.valueFilteringOperator = valueFilteringOperator;
    }

    public int getInputThreshold() {
        return inputThreshold;
    }

    public void setInputThreshold(int inputThreshold) {
        this.inputThreshold = inputThreshold;
    }


    @Override
    public Set<ValueBindingExpression> getValueBindingExpressions() {
        return ExpressionHelper.getExpressions((FilterableComponent) this);
    }
}
