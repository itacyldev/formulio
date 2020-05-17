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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.config.ComponentBuilder;
import es.jcyl.ita.frmdrd.config.ConfigNodeHelper;
import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.config.resolvers.RepositoryAttributeResolver;
import es.jcyl.ita.frmdrd.forms.FCAction;
import es.jcyl.ita.frmdrd.forms.FormEditController;
import es.jcyl.ita.frmdrd.forms.FormListController;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.datatable.UIDatatable;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Builder class to create UIForm instances from an Entity metadata information. It maps each
 * metadata property to the most most suitable field component for each table column.
 */
public class FormListControllerBuilder extends AbstractComponentBuilder<FormListController> {

    private static final Set<String> ACTION_SET = new HashSet<String>(Arrays.asList("add", "update", "cancel", "delete", "nav"));
    private static final Set<String> ENTITY_SELECTOR_SET = new HashSet<String>(Arrays.asList("datatable"));

    private static RepositoryAttributeResolver repoResolver = new RepositoryAttributeResolver();

    public FormListControllerBuilder(String tagName) {
        super(tagName, FormListController.class);
    }

    @Override
    protected void doWithAttribute(FormListController element, String name, String value) {
    }


    @Override
    protected void setupOnSubtreeStarts(ConfigNode<FormListController> node) {
        FormListController ctl = node.getElement();
        // find nested filter if exists
        List<ConfigNode> repoFilters = ConfigNodeHelper.getChildrenByTag(node, "repoFilter");
        if (CollectionUtils.isNotEmpty(repoFilters)) {
            if (repoFilters.size() > 1)
                error(String.format("Just one nested repoFilter element can be defined in 'list', " +
                        "found: []", repoFilters.size()));
            else if (repoFilters.size() == 1) {
                ctl.setFilter((Filter) repoFilters.get(0).getElement());
            }
        }
        // find entitySelector
        UIView listView = new UIView(ctl.getId() + ">view");
        ctl.setView(listView);

        // setup actions must be configured at start of he subtree, so the can be
        // used by nested elements to configure themselves if needed
        createDefaultActionNodes(node);
        UIBuilderHelper.addDefaultRepoNode(node);
        // if no repo configuration is defined, use parent
        UIBuilderHelper.setUpRepo(node, true);
    }


    /**
     * Searchs for actions in current Form file configuration. If no action if found for current list-form
     * it creates default action-nodes.
     *
     * @param node
     */
    private void createDefaultActionNodes(ConfigNode<FormListController> node) {
        if (ConfigNodeHelper.hasDescendantByTag(node, ACTION_SET)) {
            return;
        }
        ConfigNode root = ConfigNodeHelper.getRoot(node);
        List<ConfigNode> edits = ConfigNodeHelper.getChildrenByTag(root, "edit");
        String editId;
        if (edits.size() > 1) {
            throw new ConfigurationException(error("List-view with more that one edit-view " +
                    "and with no actions defined!. When you have more that one <edit/> in a form, " +
                    "you have to use <actions/> element in the <list/> to define which view will " +
                    "be navigated from the list."));
        }
        // there must be at least one create by FormConfigBuilder
        editId = edits.get(0).getId();

        String listId = node.getId();
        ConfigNode actionsNode = new ConfigNode("actions");
        node.addChild(actionsNode);
        actionsNode.addChild(createActionNode("add", listId + "#add", "Add", editId));
        actionsNode.addChild(createActionNode("update", listId + "#update", "Update", editId));
        actionsNode.addChild(createActionNode("delete", listId + "#delete", "Delete", null));
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<FormListController> node) {
        // add nested ui elements
        UIComponent[] uiComponents = ConfigNodeHelper.getUIChildren(node);
        node.getElement().getView().setChildren(uiComponents);

        setUpActions(node);
        setUpEntitySelector(node); // see issue #203650
    }

    /**
     * Searchs for actions in nested configuration
     *
     * @param node
     */
    private void setUpActions(ConfigNode<FormListController> node) {
        ConfigNode actions = ConfigNodeHelper.getFirstChildrenByTag(node, "actions");

        List<ConfigNode> actionList = actions.getChildren();
        FCAction[] lstActions = new FCAction[actionList.size()];

        for (int i = 0; i < actionList.size(); i++) {
            lstActions[i] = (FCAction) actionList.get(i).getElement();
            lstActions[i].setType(actionList.get(i).getName());
        }
        node.getElement().setActions(lstActions);
    }

    /**
     * Looks for an inner element that implements an EntitySelector interface, if no component
     * is found it creates a default dataTable. It also checks the attribute "entitySelector" is
     * properly set and references an instance of interface EntitySelector.
     *
     * @param node
     */
    private void setUpEntitySelector(ConfigNode<FormListController> node) {
        Object selector = null;
        List<ConfigNode> entitySelectors = ConfigNodeHelper.getDescendantByTag(node, ENTITY_SELECTOR_SET);
        String entitySelectorId = node.getAttribute("entitySelector");

        if (StringUtils.isNotBlank(entitySelectorId)) {
//            // try to find the referenced component in the selectors list
//            for (ConfigNode n : entitySelectors) {
//                if (n.getId().equals(entitySelectorId)) {
//                    Object element = n.getElement();
//                    if (element != null && element instanceof EntitySelector) {
//                        throw new ConfigurationException(error(String.format("The attribute 'entitySelector' " +
//                                "references an object that doesn't implements EntitySelector interfaces. " +
//                                "This attribute can be used to point to one of these components: [%s] ", ENTITY_SELECTOR_SET)));
//                    } else {
//                        selector = element;
//                    }
//                }
//            }
            throw new UnsupportedOperationException("Not supported yet!");
        } else {// the attribute is not set
            int numSelectors = entitySelectors.size();
            if (numSelectors == 0) {
                // create default selector and add to view
                UIDatatable table = createDefaultSelector(node);
                node.getElement().getView().addChild(table);
//                selector = (EntitySelector) table;
            } else if (numSelectors == 1) {
                // if there's just one entitySelector use it
                selector = entitySelectors.get(0).getElement();
            } else {
                throw new ConfigurationException(error("The <list/> form defined in " +
                        "file '${file}' has more than one EntitySelector, use the attribute 'entitySelector'" +
                        " to set the id of the main selector"));
            }
        }
//        node.getElement().setEntitySelector(selector);

    }

    /**
     * Uses datatableBuilder to create a default entitySelector with current repository
     *
     * @param node
     * @return
     */
    private UIDatatable createDefaultSelector(ConfigNode<FormListController> node) {
        ComponentBuilder<UIDatatable> builder = this.getFactory().getBuilder("datatable", UIDatatable.class);

        ConfigNode newNode = new ConfigNode("datatable");
        node.addChild(newNode);
        if(node.hasAttribute("repo")){
            newNode.setAttribute("repo", node.getAttribute("repo"));
        }
        newNode.setId("datatable" + node.getId());
        UIDatatable table = builder.build(newNode);
        newNode.setElement(table);
        builder.processChildren(newNode);

        return table;
    }


    private ConfigNode createActionNode(String action, String id, String label, String route) {
        ConfigNode node = new ConfigNode(action);
        node.setId(id);
        node.setAttribute("label", label);
        node.setAttribute("route", route);
        return node;
    }

    @Override
    protected FormListController instantiate() {
        return new FormListController("", "");
    }

}
