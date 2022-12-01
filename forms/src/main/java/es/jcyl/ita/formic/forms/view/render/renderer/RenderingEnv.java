package es.jcyl.ita.formic.forms.view.render.renderer;
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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.actions.events.UserEventInterceptor;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.context.impl.EntityContext;
import es.jcyl.ita.formic.forms.scripts.ScriptEngine;
import es.jcyl.ita.formic.forms.view.ViewStateHolder;
import es.jcyl.ita.formic.forms.view.activities.FormActivity;
import es.jcyl.ita.formic.forms.view.dag.ViewDAG;
import es.jcyl.ita.formic.forms.view.render.DeferredView;
import es.jcyl.ita.formic.forms.view.selection.SelectionManager;
import es.jcyl.ita.formic.forms.view.widget.StatefulWidget;
import es.jcyl.ita.formic.forms.view.widget.WidgetContext;
import es.jcyl.ita.formic.repo.Entity;

/**
 * Context storing object used during the rendering process to give the renderers access to commons objects
 * needed during the view construction, like entity and view contexts, access to userAction
 * interceptors and temporary store like deferred view elements.
 * During the rendering, the ExecEnvironment gathers the form contexts to bound them later to the
 * GlobalContext, so access to all form context can be made with expressions like formId.entity.property
 * or formId.view.field.
 */
public class RenderingEnv {
    /**
     * context access
     */
    CompositeContext globalContext;
    WidgetContext widgetContext;
    WidgetManager widgetManager;
    RenderingEnvFactory factory;
    /**
     * Wrapper for globalContext, used in case contxt is accesed before first WidgetContextHolder
     * has been renderer. Testing purposes mainly.
     */
    ViewWidget rootWidget;
    ViewStateHolder stateHolder; // current View state holder
    SelectionManager selectionManager = new SelectionManager();
    /**
     * view rendering
     */
    ViewDAG viewDAG;
    Map<String, List<DeferredView>> deferredViews;
    UserEventInterceptor userActionInterceptor;
    Context viewContext; // current view Android Context
    FormActivity formActivity;
    ScriptEngine scriptEngine;
    boolean restoreState = true;

    /**
     * User text typing delay controls
     */
    int inputTypingDelay = 450;
    boolean inputDelayDisabled = false;
    // current entity in context
    Entity entity;
    /**
     * Keeps the message errors to show messages during re-rendering
     */
    Map<String, BasicContext> messageMap = new HashMap<>();
    BasicContext currentMessageContext;

    RenderingEnv(ActionController actionController) {
        userActionInterceptor = new UserEventInterceptor(actionController);
    }

    protected RenderingEnv() {
    }

    /**
     * Clears composite context before starting the rendering process
     */
    public void initialize() {
        this.factory.initialize(this);
    }

    CompositeContext getGlobalContext() {
        return globalContext;
    }

    public void disableInterceptors() {
        this.userActionInterceptor.setDisabled(true);
    }

    public void enableInterceptors() {
        this.userActionInterceptor.setDisabled(false);
    }

    public WidgetContext getWidgetContext() {
        return widgetContext;
    }

    /**
     * Every time a form rendering starts, the RenderEnv stores the context and links it with
     * the formId to the global context. So relative access can be done inside the form elements,
     * but also absolute access inter-form can be achieve throught the global context references.
     *
     * @param widgetContext
     */
    public void setWidgetContext(WidgetContext widgetContext) {
        if (widgetContext == null) {
            throw new IllegalStateException("WidgetContext cannot be null!.");
        }
        this.widgetContext = widgetContext;
    }

    public UserEventInterceptor getUserActionInterceptor() {
        return userActionInterceptor;
    }

    public void clearDeferredViews() {
        if (this.deferredViews != null) {
            this.deferredViews.clear();
        }
    }

    public void addDeferred(String componentId, DeferredView view) {
        if (this.deferredViews == null) {
            this.deferredViews = new HashMap<>();
        }
        if (!this.deferredViews.containsKey(componentId)) {
            this.deferredViews.put(componentId, new ArrayList<>());
        }
        this.deferredViews.get(componentId).add(view);
    }

    public Map<String, List<DeferredView>> getDeferredViews() {
        return deferredViews;
    }

    public void setViewDAG(ViewDAG viewDAG) {
        this.viewDAG = viewDAG;
    }

    public ViewDAG getViewDAG() {
        return viewDAG;
    }

    public void setAndroidContext(Context viewContext) {
        this.viewContext = viewContext;
    }

    public Context getAndroidContext() {
        return viewContext;
    }


    public boolean isInterceptorDisabled() {
        return this.userActionInterceptor.isDisabled();
    }

    public void disableInputDelay(boolean disabled) {
        this.inputDelayDisabled = disabled;
    }

    public boolean isInputDelayDisabled() {
        return inputDelayDisabled;
    }

    public int getInputTypingDelay() {
        return inputTypingDelay;
    }

    public void setInputTypingDelay(int inputTypingDelay) {
        this.inputTypingDelay = inputTypingDelay;
    }

    public FormActivity getFormActivity() {
        return formActivity;
    }

    public void setFormActivity(FormActivity formActivity) {
        this.formActivity = formActivity;
    }

    public void setGlobalContext(CompositeContext globalContext) {
        this.globalContext = globalContext;
    }

    public void clearSelection() {
        if (this.selectionManager != null) {
            this.selectionManager.clear();
        }
    }

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    void setEntity(Entity entity) {
        // add form to context with full id
        this.globalContext.addContext(new EntityContext(entity));
        // register
        this.entity = entity;
    }

    Entity getEntity() {
        return entity;
    }

    public ViewWidget getRootWidget() {
        return rootWidget;
    }

    public void setRootWidget(ViewWidget rootWidget) {
        this.rootWidget = rootWidget;
    }

    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    public void setScriptEngine(ScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
    }

    public Map<String, BasicContext> getMessageMap() {
        return messageMap;
    }

    public BasicContext getMessageContext(String id) {
        if (!messageMap.containsKey(id)) {
            messageMap.put(id, new BasicContext("messages"));
        }
        return messageMap.get(id);
    }

    public void clearMessages() {
        this.messageMap.clear();
    }

    public void setCurrentMessageContext(UIComponent component) {
        BasicContext messageContext = getMessageContext(component.getId());
        this.currentMessageContext = messageContext;
    }

    public BasicContext getCurrentMessageContext() {
        return currentMessageContext;
    }

    public void restoreState(StatefulWidget widget) {
        if (stateHolder != null && restoreState) {
            stateHolder.restoreState(widget);
        }
    }

    public void setStateHolder(ViewStateHolder stateHolder) {
        this.stateHolder = stateHolder;
    }

    public boolean isRestoreState() {
        return restoreState;
    }

    public void setRestoreState(boolean restoreState) {
        this.restoreState = restoreState;
    }

    public WidgetManager getWidgetManager() {
        return widgetManager;
    }

    void setWidgetManager(WidgetManager widgetManager) {
        this.widgetManager = widgetManager;
    }

    public RenderingEnvFactory getFactory() {
        return factory;
    }

    public void setFactory(RenderingEnvFactory factory) {
        this.factory = factory;
    }
}

