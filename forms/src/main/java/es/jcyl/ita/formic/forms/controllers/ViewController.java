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

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.collections.CollectionUtils;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.components.FilterableComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.operations.FormEntityLoader;
import es.jcyl.ita.formic.forms.controllers.widget.WidgetController;
import es.jcyl.ita.formic.forms.repo.meta.Identificable;
import es.jcyl.ita.formic.forms.scripts.ScriptEngine;
import es.jcyl.ita.formic.forms.view.ViewStateHolder;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.widget.ControllableWidget;
import es.jcyl.ita.formic.repo.Entity;


/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Stores form configuration, view, permissions, etc. and provides operations to perform CRUD over and entity
 */
public class ViewController implements Identificable {

    protected MainController mc;
    protected String id;
    protected String name;
    protected String description;
    protected UIView view;
    protected WeakReference<FormConfig> formConfig;

    /////////////////////
    // Android UI elements
    /////////////////////
    private WeakReference<Activity> activity;
    private ViewWidget rootWidget;
    protected ViewGroup contentView; // Android view element where the UIView is rendered
    protected ViewStateHolder stateHolder = new ViewStateHolder();

    //    protected Repository repo;
    private FormEntityLoader entityLoader = new FormEntityLoader();

    /**
     * controller initialization actions
     */
    private String onBeforeRenderAction;
    private String onAfterRenderAction;

    public ViewController(String id, String name) {
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

    public UIForm getMainForm() {
        // TODO: improve this
        List<UIForm> forms = this.getView().getForms();
        return (CollectionUtils.isEmpty(forms)) ? null : forms.get(0);
    }

    public WidgetController getMainWidgetController() {
        ControllableWidget widget = (ControllableWidget) ViewHelper.findComponentWidget(this.getRootWidget(), getMainForm());
        return widget.getController();
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

    public void restoreViewPartialState() {
        stateHolder.restorePartialState(rootWidget);
    }

    public ViewStateHolder getStateHolder() {
        return stateHolder;
    }

    /****************************/
    /**  >>> Form List methods **/
    /****************************/

    /**
     * Number of entities obtained with the main repository. Used in the form list to give
     * information of the number of "form instances (table records)" registered
     *
     * @return
     */
    public long count() {
        FilterableComponent entitySelector = this.getView().getEntityList();
        return entitySelector.getRepo().count(entitySelector.getFilter());
    }

    public FilterableComponent getEntityList() {
        return this.view.getEntityList();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UIView getView() {
        return view;
    }

    public void setView(UIView view) {
        this.view = view;
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

    public void reset() {
        this.rootWidget = null;
        this.contentView = null;
        this.stateHolder.clearViewState();
        if (this.activity != null) {
            this.activity.get().finish();
        }
    }

    /***
     * LIFECYCLE HOOKS
     */
    public void onBeforeRender() {
        String method = this.onBeforeRenderAction;
        if (StringUtils.isNotBlank(method)) {
            ScriptEngine engine = mc.getScriptEngine();
            if (engine.isFunction(method)) {
                engine.callFunction(method, view);
            } else {
                engine.executeScript(method, Collections.singletonMap("this", this));
            }
        }
    }

    public void onAfterRender(View view) {
        String method = this.onAfterRenderAction;
        if (StringUtils.isNotBlank(method)) {
            ScriptEngine engine = mc.getScriptEngine();
            if (engine.isFunction(method)) {
                engine.callFunction(method, view);
            } else {
                engine.executeScript(method, Collections.singletonMap("this", view));
            }
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

    public Map<String, UIAction> getActionMap() {
        return this.view.getActionMap();
    }

    public UIAction[] getActions() {
        return this.view.getActions();
    }

    public Activity getActivity() {
        return activity.get();
    }

    public void setActivity(Activity activity) {
        this.activity = new WeakReference<>(activity);
    }

    public FormConfig getFormConfig() {
        return (formConfig == null) ? null : formConfig.get();
    }

    public void setFormConfig(FormConfig formConfig) {
        this.formConfig = new WeakReference<>(formConfig);
    }
}
