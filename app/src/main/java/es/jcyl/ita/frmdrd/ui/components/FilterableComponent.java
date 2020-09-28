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

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.query.Filter;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public interface FilterableComponent {

    void setRepo(Repository repo);

    Repository getRepo();

    void setFilter(Filter filter);

    Filter getFilter();

    String[] getMandatoryFilters();

    void setMandatoryFilters(String[] mandatoryFields);


}
