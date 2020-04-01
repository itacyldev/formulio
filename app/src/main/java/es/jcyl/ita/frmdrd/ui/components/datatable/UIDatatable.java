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

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.context.Context;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class UIDatatable extends UIComponent {

    Repository repo;

    public UIDatatable() {
        setRendererType("datatable");
    }

    // columns
    // header/footer templates
    // filters
    // sorting
    // behaviour (event handlers)
    private String route;
    // paginator / flow configuration
    // row selection


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
}
