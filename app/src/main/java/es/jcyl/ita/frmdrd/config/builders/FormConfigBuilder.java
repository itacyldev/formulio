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

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.FormConfig;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.config.resolvers.RepositoryAttributeResolver;
import es.jcyl.ita.frmdrd.forms.FCAction;
import es.jcyl.ita.frmdrd.forms.FormEditController;
import es.jcyl.ita.frmdrd.forms.FormListController;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;
import static es.jcyl.ita.frmdrd.config.DevConsole.warn;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class FormConfigBuilder extends AbstractComponentBuilder<FormConfig> {

    private FormListControllerBuilder listBuilder;
    private FormEditControllerBuilder editBuilder;

    public FormConfigBuilder(String tagName) {
        super(tagName, FormConfig.class);
    }

    @Override
    protected void doWithAttribute(FormConfig element, String name, String value) {
    }

    @Override
    protected void doConfigure(FormConfig formConfig, ConfigNode node) {
        // setup repository
        RepositoryAttributeResolver repoAttResolver = this.getFactory().getRepoAttResolver();
        Repository repo = repoAttResolver.resolve(node);
        formConfig.setRepo(repo);
    }


    @Override
    public void processChildren(ConfigNode<FormConfig> node) {
        // configure builders
        listBuilder = (FormListControllerBuilder) getFactory().getBuilder("list");
        editBuilder = (FormEditControllerBuilder) getFactory().getBuilder("edit");
        FormConfig formConfig = node.getElement();
        configureListView(formConfig, node);
        configureEditViews(formConfig, node);
        setUpDefaultActions(formConfig);
    }

    private void setUpDefaultActions(FormConfig formConfig) {
        String listId = formConfig.getList().getId();

        // if the edit has no actions set default actions (Save, Cancel Delete
        FCAction[] fcActions = defaultEditActions(listId);
        List<FormEditController> edits = formConfig.getEdits();
        for (FormEditController edit : edits) {
            if (ArrayUtils.isEmpty(edit.getActions())) {
                edit.setActions(fcActions);
            }
        }
        // if list has no actions setup default actions
        FormListController listCtl = formConfig.getList();
        if (ArrayUtils.isEmpty(listCtl.getActions())) {
            // we can set default list-actions just if there's only one (and only one) editView
            int numEditViews = edits.size();
            if (numEditViews == 0) {
                warn("List-view with no edit-views in file ${file}, no default actions added.");
            } else if (numEditViews > 1) {
                throw new ConfigurationException(error("List-view with more that one edit-view " +
                        "and with no actions defined!. When you have more that one <edit/> in a form, " +
                        "you have to use <actions/> element in the <list/> to define with view will " +
                        "be navigated from the list."));
            } else {
                // setup default views
                FormEditController editCtl = edits.get(0);
                FCAction[] listActions = defaultListActions(editCtl.getId());
                listCtl.setActions(listActions);
            }
        }
    }

    /**
     * Checks nested form-list element to attach it to current formConfig. If no form-list
     * configuration is found it creates one using current formConfig attributes.
     *
     * @param formConfig
     * @param node
     */
    private void configureListView(FormConfig formConfig, ConfigNode node) {
        // get list and edit views, check and set to configuration
        FormListController listController = null;
        List<ConfigNode> list = getChildren(node, "list");
        if (list.size() > 1) {
            throw new ConfigurationException(error(String.format("Each form file must contain just " +
                    "one 'list' element, found: [%s]", list.size())));
        } else if (list.size() == 1) {
            listController = (FormListController) list.get(0).getElement();
        } else {
            // if no list view, create
            ConfigNode listNode = node.copy();
            String listId = formConfig.getId() + "#list";
            listController = new FormListController(listId, "list");
            listNode.setElement(listController);
            listBuilder.doConfigure(listController, listNode);
            listBuilder.processChildren(listNode);
        }

        // if repo attribute is not defined use parent repo
        if (listController.getRepo() == null) {
            listController.setRepo(formConfig.getRepo());
        }
        formConfig.setList(listController);
    }


    private void configureEditViews(FormConfig formConfig, ConfigNode node) {
        List<FormEditController> edits = new ArrayList<>();

        List<ConfigNode> list = getChildren(node, "edit");
        if (list.size() > 1) {
            for (ConfigNode n : list) {
                edits.add((FormEditController) n.getElement());
            }
        } else {
            // if no edit found, create one using main node attributes
            ConfigNode editNode = node.copy();
            String listId = formConfig.getId() + "#edit";
            FormEditController editController = new FormEditController(listId, "edit");
            editNode.setElement(editController);
            editBuilder.doConfigure(editController, editNode);
            editBuilder.processChildren(editNode);
            edits.add(editController);
        }
        // if no repo is defined, use parent's
        for (FormEditController c : edits) {
            if (c.getRepo() == null) {
                c.setRepo(formConfig.getRepo());
            }
        }
        formConfig.setEdits(edits);
    }


    /**
     * Creates default actions for edit view. The given string is the id of the list controller
     * to return after cancel action.
     *
     * @param fcListId
     * @return
     */
    private FCAction[] defaultEditActions(String fcListId) {
        FCAction[] actions = new FCAction[2];
        // save and cancel
        actions[0] = new FCAction("save", "Save", fcListId);
        actions[1] = new FCAction("cancel", "Cancel", "back");
        return actions;
    }


    /**
     * Creates defaulta actions for list view. The given string is the Id of the main edit-view to
     * redirect when the user chooses an entity.
     *
     * @param fcEditId
     * @return
     */
    private FCAction[] defaultListActions(String fcEditId) {
        FCAction[] actions = new FCAction[3];
        // save and cancel
        actions[0] = new FCAction("add", "New", fcEditId);
        actions[1] = new FCAction("edit", "Edit", fcEditId);
        actions[2] = new FCAction("delete", "Delete", null);
        return actions;
    }

}
