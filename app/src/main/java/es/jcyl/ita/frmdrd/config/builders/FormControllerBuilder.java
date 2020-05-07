package es.jcyl.ita.frmdrd.config.builders;
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

import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.frmdrd.config.reader.BaseConfigNode;
import es.jcyl.ita.frmdrd.forms.FCAction;
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

//    private FormEditBuilder editBuilder = new FormEditBuilder();
//    private FormListControllerBuilder listBuilder = new FormListControllerBuilder();
    // current instance construction temporal state
    private Repository repo;
    private String id;


    public FormControllerBuilder withId(String id) {
        return this;
    }

    public FormControllerBuilder withRepo(Repository repo) {
        this.repo = repo;
        return this;
    }


    public FormController buildList(String fcId) {
        if (repo == null) {
            throw new IllegalStateException("No repo provided, cannot create the form.");
        }
        FormListController fc = new FormListController(fcId + "#list", "Form " + this.repo.getId());
        fc.setRepo(repo);
        // create views
//        UIForm listForm = listBuilder.witRepo(repo).withDataTableRoute(fcId + "#edit").build();
        UIView listView = new UIView(fc.getId() + ">view");
//        listView.addChild(listForm);
        fc.setView(listView);
        fc.setActions(defaultListActions(fcId));

        return fc;
    }

    public FCAction[] defaultListActions(String fcId) {
        FCAction[] actions = new FCAction[3];
        // save and cancel
        actions[0] = new FCAction("add", "New", fcId + "#edit");
        actions[1] = new FCAction("edit", "Cancel", fcId + "#edit");
        actions[2] = new FCAction("delete", "Delete", null);
        return actions;
    }
//
//    public FormEditController buildEdit(String fcId) {
//        if (repo == null) {
//            throw new IllegalStateException("No repo provided, cannot create the form.");
//        }
//        FormEditController fc = new FormEditController(fcId + "#edit", "Form " + this.repo.getId());
//        fc.setMainRepo(repo);
//        // create views
//        UIForm editForm = editBuilder.withRepo(this.repo).build();
//        UIView editView = new UIView(fc.getId() + ">view");
//        editView.addChild(editForm);
//        fc.setView(editView);
//        fc.setMainForm(editForm);
//        fc.setActions(defaultEditActions(fcId));
//        return fc;
//    }


    public FCAction[] defaultEditActions(String fcId) {
        FCAction[] actions = new FCAction[4];
        // save and cancel
        actions[0] = new FCAction("save", "Save", "back");
        actions[1] = new FCAction("save", "Save&Stay", null);
        actions[2] = new FCAction("delete", "Delete", "back");
        actions[3] = new FCAction("cancel", "Cancel", fcId + "#list");
        return actions;
    }


    public FormBuilderResult build() {
        FormBuilderResult result = new FormBuilderResult();
        if (StringUtils.isBlank(this.id)) {
            this.id = "fc_" + this.repo.getId();
        }
        result.list = this.buildList(this.id);
//        result.edit = this.buildEdit(this.id);
        clear();
        return result;
    }

    public class FormBuilderResult extends BaseConfigNode {
        FormEditController edit;
        FormController list;

        public FormEditController getEdit() {
            return edit;
        }

        public FormController getList() {
            return list;
        }
    }

    private void clear() {
        id = null;
        repo = null;
    }


}