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

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.frmdrd.ui.components.UIField;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Builder class to create UIForm instances from an Entity metadata information. It maps each
 * metadata property to the most most suitable field component for each table column.
 */
public class FormBuilder {

    private UIForm baseModel;
    private EntityMeta meta;
    private FieldBuilder fieldBuilder = new FieldBuilder();
    private Repository repo;


    public FormBuilder withMeta(EntityMeta meta) {
        this.meta = meta;
        return this;
    }
    public FormBuilder witRepo(Repository repo) {
        this.meta = repo.getMeta();
        this.repo = repo;
        return this;
    }

    public UIForm build() {
        UIForm model = baseModel;
        this.baseModel = null;
        // create fields from properties
        PropertyType[] properties = this.meta.getProperties();
        UIField[] kids = new UIField[properties.length];
        for (int i=0;i< properties.length;i++) {
            kids[i] = fieldBuilder.withProperty(properties[i]).build();
        }
        model.setRepo(this.repo);



        return model;
    }
}
