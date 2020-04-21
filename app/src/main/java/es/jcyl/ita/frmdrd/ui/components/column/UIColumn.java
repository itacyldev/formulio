package es.jcyl.ita.frmdrd.ui.components.column;
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

import es.jcyl.ita.frmdrd.ui.components.UIComponent;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIColumn extends UIComponent {


    // sort by, filter by, filter matching, header expression
    private String headerText;

    private UIFilter headerFilter;
    private Boolean filtering;
    private Boolean ordering = true;

    public UIColumn() {

    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public Boolean isFiltering() {
        return filtering;
    }

    public final void setFiltering(Boolean filtering) {
        this.filtering = filtering;

        if (filtering) {
            headerFilter = new UIFilter();
            headerFilter.setFilterProperty(this.getId());
            headerFilter.setOrderPropery(this.getId());
        }
    }

    public Boolean isOrdering() {
        return ordering;
    }

    public void setOrdering(Boolean ordering) {
        this.ordering = ordering;
    }

    public UIFilter getHeaderFilter() {
        return headerFilter;
    }

    public void setHeaderFilter(UIFilter filter) {
        this.headerFilter = filter;
    }

}
