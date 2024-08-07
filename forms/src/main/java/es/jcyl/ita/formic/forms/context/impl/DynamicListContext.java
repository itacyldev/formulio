package es.jcyl.ita.formic.forms.context.impl;
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

import es.jcyl.ita.formic.core.context.AbstractMapContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.db.SQLQueryFilter;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class DynamicListContext extends AbstractMapContext implements Context {

    private final Repository repo;
    private SQLQueryFilter filter;

    public DynamicListContext(Repository repo) {
        this.repo = repo;
        this.filter = new SQLQueryFilter();
    }

    public List<Entity> getList() {
        return repo.find(filter);
    }

    public SQLQueryFilter getFilter() {
        return filter;
    }

    public void setFilter(SQLQueryFilter filter) {
        this.filter = filter;
    }
}
