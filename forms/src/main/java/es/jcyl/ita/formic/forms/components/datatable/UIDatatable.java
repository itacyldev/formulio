package es.jcyl.ita.formic.forms.components.datatable;
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

import java.util.List;
import java.util.Set;

import es.jcyl.ita.formic.forms.components.EntitySelector;
import es.jcyl.ita.formic.forms.components.ExpressionHelper;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.column.UIColumn;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.query.Filter;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class UIDatatable extends UIComponent implements EntitySelector {

    Repository repo;

    // columns
    private UIColumn[] columns;
    // header/footer templates
    // filters and sorting
    private Filter filter;
    private String[] mandatoryFilters;
    // behaviour (event handlers)
    private String route;
    // paginator / flow configuration
    // row selection
    private int numFieldsToShow = 20;

    private int numVisibleRows = 10;
    private List<Entity> selectedEntities;

    public UIDatatable() {
        setRendererType("datatable");
    }

    @Override
    public List<Entity> getSelectedEntities() {
        return selectedEntities;
    }

    public void selectEntity(Entity entity) {
        this.selectedEntities.add(entity);
    }

    public Repository getRepo() {
        return repo;
    }

    public void setRepo(Repository repo) {

        this.repo = repo;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public UIColumn[] getColumns() {
        return columns;
    }

    public void setColumns(UIColumn[] columns) {
        this.columns = columns;
    }

    public void addColumn(UIColumn column) {
        int size = (this.columns == null) ? 1 : this.columns.length + 1;
        UIColumn[] newCols = new UIColumn[size];
        if (this.columns != null) {
            // copy previous values
            System.arraycopy(this.columns, 0, newCols, 0, this.columns.length);
        }
        newCols[size - 1] = column;
        this.columns = newCols;
    }

    public UIColumn getColumn(int index) {
        if (this.columns == null) {
            return null;
        } else {
            if (index < 0 || index > columns.length) {
                throw new IllegalArgumentException(String.format("Error trying to access columns " +
                                "from table [%s]. Invalid index [%s] the table has [%s] columns.",
                        this.getId(), index, columns.length));
            }
            return this.columns[index];
        }
    }

    public UIColumn getColumn(String name) {
        if (this.columns == null) {
            return null;
        } else {
            for (UIColumn c : columns) {
                if (c.getId().equals(name)) {
                    return c;
                }
            }
        }
        return null;
    }

    public int getNumVisibleRows() {
        return this.numVisibleRows;
    }

    public void setNumVisibleRows(final int numVisibleRows) {
        this.numVisibleRows = numVisibleRows;
    }

    public int getNumFieldsToShow() {
        return this.numFieldsToShow;
    }

    public void setNumFieldsToShow(int numFieldsToShow) {
        this.numFieldsToShow = numFieldsToShow;
    }

    public Filter getFilter() {
        return filter;
    }

    @Override
    public String[] getMandatoryFilters() {
        return this.mandatoryFilters;
    }

    public void setMandatoryFilters(String[] mandatoryFilters) {
        this.mandatoryFilters = mandatoryFilters;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
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
