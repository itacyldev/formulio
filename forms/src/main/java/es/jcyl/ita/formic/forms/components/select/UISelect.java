package es.jcyl.ita.formic.forms.components.select;
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

import es.jcyl.ita.formic.forms.components.ExpressionHelper;
import es.jcyl.ita.formic.forms.components.option.MultiOptionComponent;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.query.Filter;
import es.jcyl.ita.formic.forms.components.FilterableComponent;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.option.UIOption;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UISelect extends UIInputComponent implements FilterableComponent, MultiOptionComponent {

    private static final String SELECT_TYPE = "select";
    protected UIOption[] options;
    protected Repository repo;
    private boolean hasNullOption = true;
    protected Filter filter;
    private String[] mandatoryFilters;

    public Repository getRepo() {
        return repo;
    }

    public void setRepo(Repository repo) {
        this.repo = repo;
    }

    public UIOption[] getOptions() {
        return options;
    }

    public void setOptions(UIOption[] options) {
        this.options = options;
    }

    @Override
    public String getRendererType() {
        return SELECT_TYPE;
    }

    @Override
    public String getValueConverter() {
        return SELECT_TYPE;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    /**
     * No repository configured to populate select options
     *
     * @return
     */
    public boolean isStatic() {
        return (this.repo == null);
    }


    public boolean hasNullOption() {
        return hasNullOption;
    }

    public void setHasNullOption(boolean hasNullOption) {
        this.hasNullOption = hasNullOption;
    }


    @Override
    public String[] getMandatoryFilters() {
        return this.mandatoryFilters;
    }

    public void setMandatoryFilters(String[] mandatoryFilters) {
        this.mandatoryFilters = mandatoryFilters;
    }

    @Override
    public Set<ValueBindingExpression> getValueBindingExpressions() {
        Set<ValueBindingExpression> expressions = super.getValueBindingExpressions();
        // If repo filter is defined, add binding expressions to establish dependencies
        if (this.filter != null) {
            expressions.addAll(ExpressionHelper.getExpressions(this.filter.getExpression()));
        }
        return expressions;
    }
}
