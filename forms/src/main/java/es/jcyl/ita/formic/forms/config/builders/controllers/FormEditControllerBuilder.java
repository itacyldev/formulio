package es.jcyl.ita.formic.forms.config.builders.controllers;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.builders.AbstractComponentBuilder;
import es.jcyl.ita.formic.forms.config.builders.BuilderHelper;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.config.resolvers.RepositoryAttributeResolver;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.repo.query.Filter;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
public class FormEditControllerBuilder extends AbstractComponentBuilder<FormEditController> {
    private static RepositoryAttributeResolver repoResolver;

    private static final Set<String> ACTION_SET = new HashSet<String>(Arrays.asList("action", "add", "update",
            "save", "cancel", "delete", "nav"));

    public FormEditControllerBuilder(String tagName) {
        super(tagName, FormEditController.class);
    }

    @Override
    protected void doWithAttribute(FormEditController element, String name, String value) {
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<FormEditController> node) {
        FormEditController ctl = node.getElement();
        // find nested filter if exists
        List<ConfigNode> repoFilters = ConfigNodeHelper.getChildrenByTag(node, "repoFilter");
        if (CollectionUtils.isNotEmpty(repoFilters)) {
            if (repoFilters.size() > 1)
                error(String.format("Just one nested repoFilter element can be defined in " +
                        "<edit/>, found: []", repoFilters.size()));
            else if (repoFilters.size() == 1) {
                ctl.setFilter((Filter) repoFilters.get(0).getElement());
            }
        }
        UIView view = new UIView(ctl.getId() + ">view");
        view.setFormController(ctl);
        ctl.setView(view);

        // if no nested repo defined, inherit attribute from parent
        if (!ConfigNodeHelper.hasChildrenByTag(node, "repo")) {
            BuilderHelper.inheritAttribute(node, "repo");
        }

        // if no nested form, create one
        createDefaultForm(node);
        // setup actions must be configured at start of he subtree, so the can be
        // used by nested elements to configure themselves if needed
        createDefaultActionNodes(node);

        BuilderHelper.addDefaultRepoNode(node);
        // if no repo configuration is defined, use parent
        BuilderHelper.setUpRepo(node, true);
    }

    /**
     * Searchs for actions in nested configuration. It is called in the subtree start, so
     * it has to handle ConfigNodes and not elements
     *
     * @param node
     */
    private void createDefaultActionNodes(ConfigNode<FormEditController> node) {
        if (ConfigNodeHelper.hasDescendantByTag(node, ACTION_SET)) {
            // it already has a form
            return;
        }
        DevConsole.debug("No nested actions found, creating default form actions.");

        ConfigNode root = ConfigNodeHelper.getRoot(node);
        List<ConfigNode> listCtls = ConfigNodeHelper.getChildrenByTag(root, "list");
        String listId;
        if (listCtls.size() > 1) {
            throw new ConfigurationException(error("Just one <list/> element can be nested in a form configuration."));
        }
        // there must be at least one create by FormConfigBuilder
        listId = listCtls.get(0).getId();

        ConfigNode actionsNode = new ConfigNode("actions");
        node.addChild(actionsNode);

        actionsNode.addChild(createActionNode("save", listId + "#save", "Save", listId, "false"));
        actionsNode.addChild(createActionNode("cancel", listId + "#cancel", "Cancel", "back", "false"));
    }


    @Override
    protected void setupOnSubtreeEnds(ConfigNode<FormEditController> node) {
        // add nested ui elements
        UIComponent[] uiComponents = ConfigNodeHelper.getUIChildren(node);
        node.getElement().getView().setChildren(uiComponents);
        node.getElement().getView().setRoot(node.getElement().getView());

        setUpActions(node);
        setUpForms(node);
    }


    /**
     * Searchs for actions in nested configuration
     *
     * @param node
     */
    private void setUpActions(ConfigNode<FormEditController> node) {
        ConfigNode actions = ConfigNodeHelper.getFirstChildrenByTag(node, "actions");
        if (actions == null) {
            return;
        }

        List<ConfigNode> actionList = actions.getChildren();
        UIAction[] lstActions = new UIAction[actionList.size()];

        UIAction action;
        for (int i = 0; i < actionList.size(); i++) {
            action = (UIAction) actionList.get(i).getElement();
            if (StringUtils.isBlank(action.getType())) {
                action.setType(actionList.get(i).getName());
            }
            lstActions[i] = action;
        }
        node.getElement().setActions(lstActions);
    }

    private void setUpForms(ConfigNode<FormEditController> node) {
        FormEditController ctl = node.getElement();
        // get nested forms
        List<ConfigNode> forms = ConfigNodeHelper.getDescendantByTag(node, "form");
        int numForms = forms.size();
        UIForm mainForm = null;
        if (numForms == 1) {
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
        // add forms
    }

    /**
     * If current <edit/> element doesnt have a form, create one and nested all elements except
     * actions and scripts
     *
     * @return
     */
    private void createDefaultForm(ConfigNode<FormEditController> root) {
        if (ConfigNodeHelper.hasDescendantByTag(root, "form")) {
            // it already has a form
            return;
        }
        ConfigNode formNode = new ConfigNode("form");
        formNode.setId("form" + root.getId());
        if (root.hasAttribute("repo")) {
            formNode.setAttribute("repo", root.getAttribute("repo"));
        }
        List<ConfigNode> rootChildren = new ArrayList<>();
        rootChildren.add(formNode);

        List<ConfigNode> formChildren = new ArrayList<>();
        for (ConfigNode n : root.getChildren()) {
            if (n.getName().equals("actions") || n.getName().equals("script")) {
                rootChildren.add(n);
            } else {
                formChildren.add(n);
            }
        }
        root.setChildren(rootChildren);
        formNode.setChildren(formChildren);
    }


    private ConfigNode createActionNode(String action, String id, String label, String route, String registerInHistory) {
        ConfigNode node = new ConfigNode(action);
        node.setId(id);
        node.setAttribute("label", label);
        node.setAttribute("route", route);
        node.setAttribute("registerInHistory", registerInHistory);
        return node;
    }

    @Override
    protected FormEditController instantiate() {
        return new FormEditController("", "");
    }

}
