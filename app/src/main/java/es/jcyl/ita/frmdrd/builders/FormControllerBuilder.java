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

    public FormController build() {
        if (repo == null) {
            throw new IllegalStateException("No repo provided, cannot create the form.");
        }
        String fcId = "fc_" + this.repo.getId();
        // make sure there are no # in the id
        fcId = fcId.replace("#", "_");
        FormController fc = new FormController(fcId, "Form " + this.repo.getId());
        // create views
        UIForm editForm = editBuilder.witRepo(this.repo).build();
        UIView editView = new UIView("view" + editForm.getId());
        editView.addChild(editForm);
        fc.setEditView(editView);

        String route = fc.getId() + "#edit";

        UIForm listForm = listBuilder.witRepo(repo).withRoute(route).build();
        UIView listView = new UIView("view" + listForm.getId());
        listView.addChild(listForm);
        fc.setListView(listView);

        return fc;
    }
}
