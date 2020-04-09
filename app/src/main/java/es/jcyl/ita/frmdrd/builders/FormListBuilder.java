package es.jcyl.ita.frmdrd.builders;
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

import org.apache.commons.lang.RandomStringUtils;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.ui.components.UIField;
import es.jcyl.ita.frmdrd.ui.components.datatable.UIDatatable;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Builder class to create UIForm instances from an Entity metadata information. It maps each
 * metadata property to the most most suitable field component for each table column.
 */
public class FormListBuilder {

    private UIDatatable baseModel;
    private DataTableBuilder tableBuilder = new DataTableBuilder();
    private String route;

    public FormListBuilder(){
        baseModel = new UIDatatable();
    }


    public FormListBuilder witRepo(Repository repo) {
        this.baseModel.setRepo(repo);
        return this;
    }

    public FormListBuilder withFilter(Filter filter) {
        this.baseModel.setFilter(filter);
        return this;
    }
    public FormListBuilder withDataTableRoute(String route){
        this.route = route;
        return this;
    }

    public UIForm build() {
        UIDatatable table = baseModel;
        baseModel = new UIDatatable();
        if (table.getRepo() == null) {
            throw new IllegalStateException("No repo provided for the table, cannot create the form.");
        }
        table = tableBuilder.createDataTableFromRepo(table.getRepo());
        table.setRoute(this.route);

        // add form
        UIForm form = new UIForm();
        form.addChild(table);
        form.setId("form"+RandomStringUtils.randomAlphanumeric(4));

        return form;
    }
}
