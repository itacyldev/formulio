package es.jcyl.ita.formic.forms.components.view;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.forms.components.AbstractUIComponent;
import es.jcyl.ita.formic.forms.components.FilterableComponent;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.UIGroupComponent;
import es.jcyl.ita.formic.forms.components.buttonbar.UIButtonBar;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.controllers.FormController;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.query.Filter;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class UIView extends UIGroupComponent implements FilterableComponent {

    FormController formController;
    List<UIForm> forms;

    // filterable component
    private Repository repo;
    private Filter filter;
    private String[] mandatoryFilters;
    // form used to refer to the repository used to count entities
    private FilterableComponent entityList;

    // view actions
    private UIAction[] actions; // form actions ids
    private Map<String, UIAction> _actions;

    private UIForm mainForm;

    // button bars
    UIButtonBar bottomNav;
    UIButtonBar menuBar;
    UIButtonBar fabBar;

    public UIView() {
        setRendererType("view");
    }

    @Override
    public boolean isRenderChildren() {
        return true;
    }

    public List<UIForm> getForms() {
        if (this.forms == null) {
            this.forms = new ArrayList<UIForm>();
            findForms(this, this.forms);
        }
        return this.forms;
    }

    /**
     * Recursively go over the component tree storing forms in the given array
     *
     * @param root
     * @param forms
     */
    private void findForms(UIComponent root, List<UIForm> forms) {
        if (root instanceof UIForm) {
            forms.add((UIForm) root);
        } else {
            if (root.hasChildren()) {
                for (UIComponent c : root.getChildren()) {
                    findForms(c, forms);
                }
            }
        }
    }

    public FormController getFormController() {
        return formController;
    }

    public void setFormController(FormController formController) {
        this.formController = formController;
    }

    /****************/
    /** ToolBars **/
    /****************/

    public UIButtonBar getBottomNav() {
        return bottomNav;
    }

    public void setBottomNav(UIButtonBar bottomNav) {
        this.bottomNav = bottomNav;
    }

    public UIButtonBar getMenuBar() {
        return menuBar;
    }

    public void setMenuBar(UIButtonBar menuBar) {
        this.menuBar = menuBar;
    }

    public UIButtonBar getFabBar() {
        return fabBar;
    }

    public void setFabBar(UIButtonBar fabBar) {
        this.fabBar = fabBar;
    }

    /****************/
    /** Actions **/
    /****************/

    public UIAction[] getActions() {
        return actions;
    }

    public Map<String, UIAction> getActionMap() {
        return _actions;
    }

    public void addAction(String actionId, UIAction action) {
        if (_actions == null) {
            _actions = new HashMap<>();
        }
        _actions.put(actionId, action);
    }

    public void setActions(UIAction[] actions) {
        this.actions = actions;
        for (UIAction action : actions) {
            addAction(action.getId(), action);
        }
    }

    public UIAction getAction(String name) {
        if (this.actions == null) {
            return null;
        } else {
            for (UIAction action : actions) {
                if (name.equalsIgnoreCase(action.getType())) {
                    return action;
                }
            }
            return null;
        }
    }
    /****************/
    /** Filterable **/
    /****************/

    @Override
    public Repository getRepo() {
        return repo;
    }

    @Override
    public void setRepo(Repository repo) {
        this.repo = repo;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public String[] getMandatoryFilters() {
        return mandatoryFilters;
    }

    @Override
    public void setMandatoryFilters(String[] mandatoryFilters) {
        this.mandatoryFilters = mandatoryFilters;
    }
    public FilterableComponent getEntityList() {
        return entityList;
    }

    public void setEntityList(FilterableComponent entityList) {
        this.entityList = entityList;
    }


    public UIForm getMainForm() {
        return mainForm;
    }

    public void setMainForm(UIForm mainForm) {
        this.mainForm = mainForm;
    }
}
