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

import java.util.List;

import es.jcyl.ita.formic.forms.components.buttonbar.UIButtonBar;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.builders.AbstractComponentBuilder;
import es.jcyl.ita.formic.forms.config.builders.BuilderHelper;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.controllers.FormListController;

import static es.jcyl.ita.formic.forms.config.DevConsole.debug;
import static es.jcyl.ita.formic.forms.config.DevConsole.error;
import static es.jcyl.ita.formic.forms.config.DevConsole.info;
import static es.jcyl.ita.formic.forms.config.builders.controllers.UIViewBuilder.ENTITY_SELECTOR_SET;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Builder class to create UIForm instances from an Entity metadata information. It maps each
 * metadata property to the most most suitable field component for each table column.
 */
public class FormListControllerBuilder extends AbstractComponentBuilder<FormListController> {


    public FormListControllerBuilder(String tagName) {
        super(tagName, FormListController.class);
    }

    @Override
    protected void doWithAttribute(FormListController element, String name, String value) {
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<FormListController> node) {
        BuilderHelper.createDefaultView(node);
        // create default datatable if needed
        createDefaultEntitySelector(node);
        // create default fabButtonBar if needed
        ConfigNode fabBar = findOrCreateFabButtonBar(node);
        if (fabBar != null) {
            setupRouteOnSelector(node, fabBar);
        }
    }


    /**
     * In case current formController doesn't have an entityList component defined, it creates a
     * default datatable connected to current repository.
     *
     * @param root
     * @return
     */
    private void createDefaultEntitySelector(ConfigNode<FormListController> root) {
        ConfigNode viewNode = root.getChildren().get(0);

        if (ConfigNodeHelper.hasDescendantByTag(viewNode, ENTITY_SELECTOR_SET)) {
            // it already has a datatable
            return;
        }
        ConfigNode tableNode = new ConfigNode("datatable");
        tableNode.setId("datatable" + root.getId());
        if (root.hasAttribute("repo")) {
            tableNode.setAttribute("repo", root.getAttribute("repo"));
        }
        viewNode.addChild(tableNode);
    }

    /**
     * Searchs for actions in current Form file configuration. If no action if found for current list-form
     * it creates default action-nodes.
     *
     * @param node
     * @return
     */
    private ConfigNode findOrCreateFabButtonBar(ConfigNode<FormListController> node) {
        ConfigNode viewNode = node.getChildren().get(0);

        // look for buttonbars
        List<ConfigNode> children = viewNode.getChildren();
        for (ConfigNode n : children) {
            if (n.getName().toLowerCase().equals("buttonbar")) {
                String type = n.getAttribute("type");
                if (!StringUtils.isBlank(type)
                        && type.toUpperCase().equals(UIButtonBar.ButtonBarType.FAB.name())) {
                    return n;
                }
                break;
            }
        }
        debug("No nested fabBar found, creating default one.");

        ConfigNode root = ConfigNodeHelper.getRoot(node);
        List<ConfigNode> edits = ConfigNodeHelper.getChildrenByTag(root, "edit");

        String editId;
        if (edits.size() > 1) {
            info("List-view with more that one edit-view " +
                    "and with no actions defined!. When you have more that one <edit/> in a form, " +
                    "you have to use <actions/> element in the <list/> to define which view will " +
                    "be navigated from the list, or use a <datatable/datalist> element.");
            return null;
        }
        // there must be at least one create by FormConfigBuilder
        editId = edits.get(0).getId();
        String listId = node.getId();

        // create default fabBar
        ConfigNode buttonbar = new ConfigNode("buttonbar");
        buttonbar.setAttribute("type", UIButtonBar.ButtonBarType.FAB.name());
        viewNode.addChild(buttonbar);

        buttonbar.addChild(createButton("nav", listId + "#add", "Add", editId));
//        buttonbar.addChild(createActionNode("update", listId + "#update", "Update", editId));
//        buttonbar.addChild(createActionNode("delete", listId + "#delete", "Delete", null));
        return buttonbar;
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<FormListController> node) {
        // link view and controller
        ConfigNode viewNode = node.getChildren().get(0);
        UIView view = (UIView) viewNode.getElement();
        FormListController fController = node.getElement();
        view.setFormController(fController);
        fController.setView(view);
    }


    /**
     * Looks for an EntitySelector component and sets on it the default route using the first
     * button from the fabButtonBar that has the route attribute set.
     *
     * @param root
     * @param fabBar
     */
    private void setupRouteOnSelector(ConfigNode<FormListController> root, ConfigNode fabBar) {

        List<ConfigNode> selectors = ConfigNodeHelper.getDescendantByTag(root, ENTITY_SELECTOR_SET);

        // if there's just one and route attribute is not set
        if (selectors == null || selectors.size() != 1
                || selectors.get(0).hasAttribute("route")) {
            return;
        }
        ConfigNode entitySelector = selectors.get(0);

        String route = null;
        List<ConfigNode> kids = fabBar.getChildren();
        // find the first button with the route attribute set
        for (ConfigNode c : kids) {
            if (c.getName().equals("button")) {
                // if it hast the route attribute use it, in other case look for a nested action
                if(c.hasAttribute("route")){
                    route = c.getAttribute("route");
                } else if(c.hasChildren()){
                    ConfigNode actionNode = (ConfigNode) c.getChildren().get(0);
                    route = actionNode.getAttribute("route");
                }
                // get nested action
                if(StringUtils.isNotBlank(route)){
                    break; // stop looking
                }
            }
        }
        entitySelector.setAttribute("route", route);
    }

    /**
     * Creates a button and nested UserAction nodes for formList user default actions
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

        ConfigNode buttonNode = new ConfigNode("button");
        buttonNode.setAttribute("label", label);
        buttonNode.setId(id);
        buttonNode.addChild(actionNode);
        return buttonNode;
    }

    @Override
    protected FormListController instantiate(ConfigNode<FormListController> node) {
        return new FormListController("", "");
    }

}
