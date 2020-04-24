package es.jcyl.ita.frmdrd.ui.components.datatable;
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

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.el.ValueBindingExpression;
import es.jcyl.ita.frmdrd.ui.components.ExpressionHelper;
import es.jcyl.ita.frmdrd.ui.components.FilterableComponent;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.column.UIColumn;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class UIDatatable extends UIComponent implements FilterableComponent {

    Repository repo;

    // columns
    private UIColumn[] columns;
    // header/footer templates
    // filters and sorting
    private Filter filter;
    // behaviour (event handlers)
    private String route;
    // paginator / flow configuration
    // row selection
    private int numFieldsToShow = 20;

    public UIDatatable() {
        setRendererType("datatable");
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

    public int getNumFieldsToShow() {
        return this.numFieldsToShow;
    }

    public void setNumFieldsToShow(int numFieldsToShow) {
        this.numFieldsToShow = numFieldsToShow;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public Set<ValueBindingExpression> getValueBindingExpressions() {
        return ExpressionHelper.getExpressions((FilterableComponent) this);
    }

}
