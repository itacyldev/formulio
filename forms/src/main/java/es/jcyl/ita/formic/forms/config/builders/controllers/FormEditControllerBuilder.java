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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.formic.forms.components.buttonbar.UIButtonBar;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.builders.AbstractComponentBuilder;
import es.jcyl.ita.formic.forms.config.builders.BuilderHelper;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.config.resolvers.RepositoryAttributeResolver;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.controllers.ViewController;

import static es.jcyl.ita.formic.forms.config.DevConsole.debug;
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
        // Do nothing
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<FormEditController> node) {
        BuilderHelper.createDefaultView(node);

        // if no nested form, create one
        createDefaultForm(node);
        // setup actions must be configured at start of he subtree, so the can be
        // used by nested elements to configure themselves if needed
        createDefaultButtonbar(node);
    }

    /**
     * Searchs for default buttonbar of type "bottom", if none is defined creates one with default
     * save/cancel actions.
     *
     * @param node
     */
    private void createDefaultButtonbar(ConfigNode<FormEditController> node) {
        ConfigNode viewNode = node.getChildren().get(0);
        // look for buttonbars
        List<ConfigNode> children = viewNode.getChildren();
        boolean hasBottomBar = false;
        for (ConfigNode n : children) {
            if (n.getName().toLowerCase().equals("buttonbar")) {
                String type = n.getAttribute("type");
                hasBottomBar = !StringUtils.isBlank(type)
                        && type.toUpperCase().equals(UIButtonBar.ButtonBarType.BOTTOM.toString());
                break;
            }
        }
        if (hasBottomBar) {
            return;
        }
        debug("No nested bottombar found, creating default one.");

        ConfigNode root = ConfigNodeHelper.getRoot(node);
        List<ConfigNode> listCtls = ConfigNodeHelper.getChildrenByTag(root, "list");
        String listId;
        if (listCtls.size() > 1) {
            throw new ConfigurationException(error("Just one <list/> element can be nested in a form configuration."));
        }
        // there must be at least one create by FormConfigBuilder
        listId = listCtls.get(0).getId();

        ConfigNode buttonbar = new ConfigNode("buttonbar");
        buttonbar.setAttribute("type", UIButtonBar.ButtonBarType.BOTTOM.name());
        viewNode.addChild(buttonbar);

        buttonbar.addChild(createButton("save", listId + "#save", "Save", listId));
        buttonbar.addChild(createButton("cancel", listId + "#cancel", "Cancel", "back"));
    }


    @Override
    protected void setupOnSubtreeEnds(ConfigNode<FormEditController> node) {
        // link view and controller
        ConfigNode viewNode = node.getChildren().get(0);
        UIView view = (UIView) viewNode.getElement();
        ViewController fController = node.getElement();
        view.setFormController(fController);
        fController.setView(view);
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
        ConfigNode viewNode = ConfigNodeHelper.getDescendantByTag(root, "view").get(0);
        ConfigNode formNode = new ConfigNode("form");
        formNode.setId("form" + root.getId());
        if (root.hasAttribute("repo")) {
            formNode.setAttribute("repo", root.getAttribute("repo"));
        }
        List<ConfigNode> viewChildren = new ArrayList<>();
        viewChildren.add(formNode);

        List<ConfigNode> formChildren = new ArrayList<>();
        List<ConfigNode> children = viewNode.getChildren();
        for (ConfigNode n : children) {
            if (n.getName().equals("actions") || n.getName().equals("script") ||
                    isViewButtonBar(n)) {
                viewChildren.add(n);
            } else {
                formChildren.add(n);
            }
        }
        viewNode.setChildren(viewChildren);
        formNode.setChildren(formChildren);
    }

    /**
     * Checks if the configNode is defining a view ButtonBar
     *
     * @param n
     * @return
     */
    private boolean isViewButtonBar(ConfigNode n) {
        String name = n.getName();
        if (!name.toLowerCase().equals("buttonbar")) {
            return false;
        } else {
            String type = n.getAttribute("type");
            if (type == null) {
                return false;
            } else {
                type = type.toUpperCase();
                return type.equals(UIButtonBar.ButtonBarType.BOTTOM.toString()) ||
                        type.equals(UIButtonBar.ButtonBarType.FAB.toString()) ||
                        type.equals(UIButtonBar.ButtonBarType.MENU.toString());
            }
        }
    }

    /**
     * Creates a button and nested action to show in the form's bottom nav
     * @param action
     * @param id
     * @param label
     * @param route
     * @return
     */
    private ConfigNode createButton(String action, String id, String label, String route) {
        ConfigNode actionNode = new ConfigNode("action");
        actionNode.setAttribute("route", route);
        actionNode.setAttribute("type", action);
        actionNode.setAttribute("registerInHistory", "false");
        actionNode.setAttribute("restoreView", "true");
        actionNode.setAttribute("popHistory", "1");

        ConfigNode buttonNode = new ConfigNode("button");
        buttonNode.setAttribute("label", label);
        buttonNode.setId(id);
        buttonNode.addChild(actionNode);
        return buttonNode;
    }

    @Override
    protected FormEditController instantiate(ConfigNode<FormEditController> node) {
        return new FormEditController("", "");
    }

}
