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
import org.mini2Dx.collections.CollectionUtils;

import java.util.List;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.config.resolvers.RepositoryAttributeResolver;
import es.jcyl.ita.frmdrd.forms.FCAction;
import es.jcyl.ita.frmdrd.forms.FormEditController;
import es.jcyl.ita.frmdrd.forms.FormListController;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
public class FormEditControllerBuilder extends AbstractComponentBuilder<FormEditController> {
    private static RepositoryAttributeResolver repoResolver;


    public FormEditControllerBuilder(String tagName) {
        super(tagName, FormEditController.class);
    }

    @Override
    protected void doWithAttribute(FormEditController element, String name, String value) {
    }


    @Override
    protected void doConfigure(FormEditController ctl, ConfigNode node) {
        repoResolver = getFactory().getRepoAttResolver();
        Repository repo = repoResolver.resolve(node);
        ctl.setRepo(repo);

        // find nested filter if exists
        List<ConfigNode> repoFilters = getChildren(node, "repoFilter");
        if (CollectionUtils.isNotEmpty(repoFilters)) {
            if (repoFilters.size() > 1)
                error(String.format("Just one nested repoFilter element can be defined in 'list', found: []", repoFilters.size()));
            else if (repoFilters.size() == 1) {
                ctl.setFilter((Filter) repoFilters.get(0).getElement());
            }
        }
        // find entitySelector
        UIView view = new UIView(ctl.getId() + ">view");
        ctl.setView(view);

        // check if there's a defined form
        setUpForms(ctl, node);

        // add first-level nested ui elements
        UIComponent[] uiComponents = getUIChildren(node);
        view.setChildren(uiComponents);

//        ctl.setActions(defaultListActions(fcId));


//        UIForm editForm = editBuilder.withRepo(this.repo).build();
//        UIView editView = new UIView(fc.getId() + ">view");
//        editView.addChild(editForm);
//        fc.setView(editView);
//        fc.setMainForm(editForm);
//        fc.setActions(defaultEditActions(fcId));

    }

    private void setUpForms(FormEditController ctl, ConfigNode node) {
        // get nested forms
        List<ConfigNode> forms = getNestedByTag(node, "form");
        int numForms = forms.size();
        UIForm mainForm = null;
        if (numForms == 0) {
            // create default form using current node attributes
            mainForm = createDefaultForm(ctl, node);
            ctl.getView().addChild(mainForm);
        } else if (numForms == 1) {
            mainForm = (UIForm) forms.get(0).getElement();
        } else {
            // if more that one form is defined, the mainForm att must be set
            String mainFormId = node.getAttribute("mainForm");
            if (StringUtils.isBlank(mainFormId)) {
                throw new ConfigurationException(error(String.format("More than one form is defined in edit " +
                        "view [%s] in file ${file}, use attribute 'mainForm' to refer the form to " +
                        "be use to load main entity.", ctl.getId())));
            } else {
                // find main form by its id
                boolean found = false;
                for (ConfigNode n : forms) {
                    if (mainFormId.equals(n.getId())) {
                        mainForm = (UIForm) n.getElement();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new ConfigurationException(error(String.format("No form found with Id [%s]," +
                                    " check the 'mainForm' attribute in edit view [%s] in file ${file}",
                            mainFormId, ctl.getId())));
                }
            }

        }
        ctl.setMainForm(mainForm);
    }

    /**
     * Gets form builder and use current node values to create the
     *
     * @param ctl
     * @param node
     * @return
     */
    private UIForm createDefaultForm(FormEditController ctl, ConfigNode node) {
        ComponentBuilder<UIForm> formBuilder = this.getFactory().getBuilder("form", UIForm.class);
        UIForm form = formBuilder.build(node);
        return form;
    }

    public FCAction[] defaultListActions(String fcId) {
        FCAction[] actions = new FCAction[3];
        // save and cancel
        actions[0] = new FCAction("add", "New", fcId + "#edit");
        actions[1] = new FCAction("edit", "Edit", fcId + "#edit");
        actions[2] = new FCAction("delete", "Delete", null);
        return actions;
    }

    @Override
    protected FormEditController instantiate() {
        return new FormEditController("", "");
    }

    @Override
    public void processChildren(ConfigNode<FormEditController> node) {
        // add nested ui elements
        UIComponent[] uiComponents = getUIChildren(node);
        node.getElement().getView().setChildren(uiComponents);
    }
}
