package es.jcyl.ita.frmdrd.ui.components.select;
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

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.UIInputComponent;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UISelect extends UIInputComponent {

    private static final String SELECT_TYPE = "select";
    protected UIOption[] options;
    protected Repository repo;
    protected Filter filter;

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
}
