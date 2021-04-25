package es.jcyl.ita.formic.forms.controllers;
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

import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.components.FilterableComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.controllers.operations.FormEntityLoader;
import es.jcyl.ita.formic.forms.repo.meta.Identificable;
import es.jcyl.ita.formic.forms.scripts.ScriptEngine;
import es.jcyl.ita.formic.forms.view.ViewStateHolder;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.query.Filter;


/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Stores form configuration, view, permissions, etc. and provides operations to perform CRUD over and entity
 */
public abstract class FormController implements Identificable, FilterableComponent {
    protected MainController mc;
    protected String id;
    protected String name;
    protected UIView view;
    protected Repository repo;
    protected Filter filter;
    protected ViewGroup contentView; // Android view element where the UIView is rendered
    protected ViewStateHolder stateHolder = new ViewStateHolder();
    private UIAction[] actions; // form actions ids
    private Map<String, UIAction> _actions;
    private FormEntityLoader entityLoader = new FormEntityLoader();
    private String[] mandatoryFilters;

    /**
     * controller initialization actions
     */
    private String onBeforeRenderAction;
    private String onAfterRenderAction;
    private ViewWidget rootWidget;

    public FormController(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Loads related entity and sets it in the form contexts
     */
    public void load(CompositeContext globalCtx) {
        Entity entity;
        // load all forms included in the view
        for (UIForm form : this.view.getForms()) {
            entity = entityLoader.load(globalCtx, form);
            form.setEntity(entity);
        }
    }

    public EditableRepository getEditableRepo() {
        return getEditableRepo(this.repo);

    }

    public EditableRepository getEditableRepo(Repository repo) {
        try {
            return (EditableRepository) repo;
        } catch (ClassCastException e) {
            throw new FormException(String.format("You can't use a readonly repository to modify " +
                    "entity data repoId:[%s].", repo.getId()));
        }
    }

    public UIForm getMainForm() {
        // TODO: improve this
        List<UIForm> forms = this.getView().getForms();
        return (CollectionUtils.isEmpty(forms)) ? null : forms.get(0);
    }

    /****************************/
    /** Save/Restore state **/
    /****************************/

    public void saveViewState() {
        ViewWidget rootWidget = this.getRootWidget();
        stateHolder.saveState(rootWidget);
    }

    public void restoreViewState() {
        stateHolder.restoreState(rootWidget);
    }


    /****************************/
    /** GETTER/SETTERS **/
    /****************************/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public UIView getView() {
        return view;
    }

    public void setView(UIView view) {
        this.view = view;
    }

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

    public Repository getRepo() {
        return repo;
    }

    public void setRepo(Repository repo) {

        this.repo = repo;

    }

    public Filter getFilter() {
        return filter;
    }

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

    public boolean hasAction(String name) {
        return getAction(name) != null;
    }

    public ViewGroup getContentView() {
        return contentView;
    }

    public void setContentView(ViewGroup contentView) {
        if (contentView == null) {
            throw new FormException("The content View cannot be null!. " + this.getId());
        }
        this.contentView = contentView;
    }

    /***
     * LIFECYCLE HOOKS
     */

    public void onBeforeRender() {
        ScriptEngine engine = mc.getScriptEngine();
        if (StringUtils.isNotBlank(this.onBeforeRenderAction)) {
            engine.callFunction(this.getId(), this.onBeforeRenderAction, this);
        }
    }

    public void onAfterRender(View view) {
        ScriptEngine engine = mc.getScriptEngine();
        if (StringUtils.isNotBlank(this.onAfterRenderAction)) {
            engine.callFunction(this.getId(), this.onAfterRenderAction, view);
        }
    }

    public String getOnBeforeRenderAction() {
        return onBeforeRenderAction;
    }

    public void setOnBeforeRenderAction(String onBeforeRenderAction) {
        this.onBeforeRenderAction = onBeforeRenderAction;
    }

    public String getOnAfterRenderAction() {
        return onAfterRenderAction;
    }

    public void setOnAfterRenderAction(String onAfterRenderAction) {
        this.onAfterRenderAction = onAfterRenderAction;
    }

    public MainController getMc() {
        return mc;
    }

    public void setMc(MainController mc) {
        this.mc = mc;
    }

    public void setRootWidget(ViewWidget widget) {
        this.rootWidget = widget;
    }

    public ViewWidget getRootWidget() {
        return rootWidget;
    }
}
