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

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.frmdrd.forms.FormController;
import es.jcyl.ita.frmdrd.forms.FormEditController;
import es.jcyl.ita.frmdrd.forms.FormListController;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Builder to create list-edit views from a repository.
 */
public class FormControllerBuilder {

    private Repository repo;
    private FormEditBuilder editBuilder = new FormEditBuilder();
    private FormListBuilder listBuilder = new FormListBuilder();

    public FormControllerBuilder withRepo(Repository repo) {
        this.repo = repo;
        return this;
    }

    public FormBuilderResult build() {
        FormBuilderResult result = new FormBuilderResult();
        String fcId = "fc_" + this.repo.getId();
        result.list = this.buildList(fcId);
        result.edit = this.buildEdit(fcId);
        return result;
    }

    public FormController buildList(String fcId) {
        if (repo == null) {
            throw new IllegalStateException("No repo provided, cannot create the form.");
        }
        FormListController fc = new FormListController(fcId + "#list", "Form " + this.repo.getId());
        fc.setMainRepo(repo);
        fc.setEditRoute(fcId + "#edit");
        // create views
        UIForm listForm = listBuilder.witRepo(repo).withDataTableRoute(fcId + "#edit").build();
        UIView listView = new UIView(fc.getId() + ">view");
        listView.addChild(listForm);
        fc.setView(listView);

        return fc;
    }

    public FormEditController buildEdit(String fcId) {
        if (repo == null) {
            throw new IllegalStateException("No repo provided, cannot create the form.");
        }
        FormEditController fc = new FormEditController(fcId + "#edit", "Form " + this.repo.getId());
        fc.setMainRepo(repo);
        // create views
        UIForm editForm = editBuilder.witRepo(this.repo).build();
        UIView editView = new UIView(fc.getId() + ">view");
        editView.addChild(editForm);
        fc.setView(editView);
        fc.setRouteAfterSave(fcId + "#list");
        fc.setMainForm(editForm);
        return fc;
    }

    public class FormBuilderResult {
        FormEditController edit;
        FormController list;

        public FormEditController getEdit() {
            return edit;
        }

        public FormController getList() {
            return list;
        }
    }
}
