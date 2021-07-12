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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.formic.forms.components.FilterableComponent;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.buttonbar.UIButtonBar;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.builders.AbstractComponentBuilder;
import es.jcyl.ita.formic.forms.config.builders.BuilderHelper;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.repo.query.Filter;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

/**
 * Defines UIView configuration from XML node structure.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIViewBuilder extends AbstractComponentBuilder<UIView> {
    public static final Set<String> ENTITY_SELECTOR_SET = new HashSet<String>(Arrays.asList("datatable", "datalist"));


    public UIViewBuilder() {
        super("view", UIView.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<UIView> node) {
        UIView view = node.getElement();
        // find nested filter if exists
        List<ConfigNode> repoFilters = ConfigNodeHelper.getChildrenByTag(node, "repoFilter");
        if (CollectionUtils.isNotEmpty(repoFilters)) {
            if (repoFilters.size() > 1)
                error(String.format("Just one nested repoFilter element can be defined in " +
                        "the view, found: []", repoFilters.size()));
            else if (repoFilters.size() == 1) {
                view.setFilter((Filter) repoFilters.get(0).getElement());
            }
        }

        // if no nested repo defined, inherit attribute from parent
        if (!ConfigNodeHelper.hasChildrenByTag(node, "repo")) {
            BuilderHelper.inheritAttribute(node, "repo");
        }
        BuilderHelper.addDefaultRepoNode(node);
        // if no repo configuration is defined, use parent
        BuilderHelper.setUpRepo(node, true);
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode node) {
        setupToolBars(node);
        setUpActions(node);
        setUpEntityList(node); // see issue #203650
        setUpForms(node);
        // set view as root element for all descendant
        UIView view = (UIView) node.getElement();
        view.setRoot(view);
    }

    private void setupToolBars(ConfigNode<UIView> node) {
        UIComponent[] uiComponents = ConfigNodeHelper.getUIChildren(node);

        UIView view = node.getElement();
        // Look for defined view buttonbars (fab,bottom,menu) and set them in the UIView element
        List<UIComponent> toRemove = new ArrayList<>();
        String type;
        for (UIComponent component : uiComponents) {
            if (component instanceof UIButtonBar) {
                type = ((UIButtonBar) component).getType();
                UIButtonBar.ButtonBarType buttonBarType = UIButtonBar.ButtonBarType.valueOf(type.toUpperCase());
                switch (buttonBarType) {
                    case FAB:
                        view.setFabBar((UIButtonBar) component);
                        toRemove.add(component);
                        break;
                    case MENU:
                        view.setMenuBar((UIButtonBar) component);
                        toRemove.add(component);
                        break;
                    case BOTTOM:
                        view.setBottomNav((UIButtonBar) component);
                        toRemove.add(component);
                        break;
                }
            }
        }
        // remove main buttonbars from the content of the view
        for (UIComponent element : toRemove) {
            uiComponents = ArrayUtils.removeElement(uiComponents, element);
        }
        view.setChildren(uiComponents);
    }


    /**
     * Looks for an inner element that implements an FilterableComponent interface, if no component
     * is found it creates a default dataTable. It also checks the attribute "entityList" is
     * properly set and references an instance of interface EntitySelector.
     *
     * @param node
     */
    private void setUpEntityList(ConfigNode<UIView> node) {
        Object selector;
        List<ConfigNode> entityList = ConfigNodeHelper.getDescendantByTag(node, ENTITY_SELECTOR_SET);
        String entityListId = node.getAttribute("entityList");

        if (StringUtils.isNotBlank(entityListId)) {
            throw new UnsupportedOperationException("Not supported yet!");
        } else {// the attribute is not set
            int numSelectors = entityList.size();
            if (numSelectors == 1) {
                // if there's just one filterableComponent use it
                selector = entityList.get(0).getElement();
                // setup route
                node.getElement().setEntityList((FilterableComponent) selector);
            }
        }
    }


    private void setUpForms(ConfigNode<UIView> node) {
        UIView view = node.getElement();
        // get nested forms
        List<ConfigNode> forms = ConfigNodeHelper.getDescendantByTag(node, "form");
        int numForms = forms.size();
        UIForm mainForm = null;
        if (numForms == 0) {
            return;
        }
        if (numForms == 1) {
            mainForm = (UIForm) forms.get(0).getElement();
        } else {
            // if more that one form is defined, the mainForm att must be set
            String mainFormId = node.getAttribute("mainForm");
            if (StringUtils.isBlank(mainFormId)) {
                throw new ConfigurationException(error(String.format("More than one form is defined in " +
                        "view [%s] in file ${file}, use attribute 'mainForm' to refer the form to " +
                        "be use to load main entity.", view.getId())));
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
                                    " check the 'mainForm' attribute in view [%s] in file ${file}",
                            mainFormId, view.getId())));
                }
            }

        }
        view.setMainForm(mainForm);
    }

    /**
     * Searchs for actions in nested configuration
     *
     * @param node
     */
    public void setUpActions(ConfigNode<UIView> node) {
        List<ConfigNode> lst = ConfigNodeHelper.getChildrenByTag(node, "actions");
        if (CollectionUtils.isEmpty(lst)) {
            return;
        }
        ConfigNode actions = lst.get(0);
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
        UIView view = node.getElement();
        view.setActions(lstActions);
    }
}
