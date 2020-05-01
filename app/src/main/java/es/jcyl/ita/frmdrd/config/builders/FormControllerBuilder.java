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
import org.xmlpull.v1.XmlPullParser;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.frmdrd.builders.FormEditBuilder;
import es.jcyl.ita.frmdrd.builders.FormListBuilder;
import es.jcyl.ita.frmdrd.config.parser.AbstractComponentBuilder;
import es.jcyl.ita.frmdrd.config.parser.ConfigConsole;
import es.jcyl.ita.frmdrd.config.parser.ConfigNode;
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
public class FormControllerBuilder extends AbstractComponentBuilder {

    private FormEditBuilder editBuilder = new FormEditBuilder();
    private FormListBuilder listBuilder = new FormListBuilder();
    // current instance construction temporal state
    private String id;
    private Repository repo;

    public FormControllerBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public FormControllerBuilder withRepo(Repository repo) {
        this.repo = repo;
        return this;
    }

    @Override
    protected void doWithAttribute(String name, String value) {

    }

    public FormBuilderResult build() {
        FormBuilderResult result = new FormBuilderResult();
        if (StringUtils.isBlank(this.id)) {
            this.id = "fc_" + this.repo.getId();
        }
        result.list = this.buildList(this.id);
        result.edit = this.buildEdit(this.id);
        clear();
        return result;
    }

    @Override
    public void addText(String text) {

    }

    @Override
    public void addChild(String currentTag, ConfigNode component) {

    }

    @Override
    public void setConsole(ConfigConsole console) {

    }

    @Override
    public void setParser(XmlPullParser xpp) {

    }

    public FormController buildList(String fcId) {
        if (repo == null) {
            throw new IllegalStateException("No repo provided, cannot create the form.");
        }
        FormListController fc = new FormListController(fcId + "#list", "Form " + this.repo.getId());
        fc.setMainRepo(repo);
        // create views
        UIForm listForm = listBuilder.witRepo(repo).withDataTableRoute(fcId + "#edit").build();
        UIView listView = new UIView(fc.getId() + ">view");
        listView.addChild(listForm);
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
        fc.setMainForm(editForm);
        fc.setActions(defaultEditActions(fcId));
        return fc;
    }


    public FCAction[] defaultEditActions(String fcId) {
        FCAction[] actions = new FCAction[4];
        // save and cancel
        actions[0] = new FCAction("save", "Save", "back");
        actions[1] = new FCAction("save", "Save&Stay", null);
        actions[2] = new FCAction("delete", "Delete", "back");
        actions[3] = new FCAction("cancel", "Cancel", fcId + "#list");
        return actions;
    }

    public class FormBuilderResult extends ConfigNode {
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
