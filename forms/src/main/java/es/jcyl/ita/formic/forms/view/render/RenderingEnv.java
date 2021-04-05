package es.jcyl.ita.formic.forms.view.render;
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
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.context.ContextUtils;
import es.jcyl.ita.formic.forms.context.impl.FormContext;
import es.jcyl.ita.formic.forms.view.activities.FormActivity;
import es.jcyl.ita.formic.forms.view.dag.ViewDAG;
import es.jcyl.ita.formic.forms.view.selection.SelectionManager;

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
    private CompositeContext globalContext;
    private FormContext formContext;
    private CompositeContext combinedContext;
    private List<FormContext> currentFormContexts;
    private SelectionManager selectionManager = new SelectionManager();
    /**
     * view rendering
     */
    private ViewDAG viewDAG;
    private Map<String, DeferredView> deferredViews;
    private ViewUserActionInterceptor userActionInterceptor;
    private Context viewContext; // current view Android Context
    private View viewRoot;
    private FormActivity formActivity;
    /**
     * User text typing delay controls
     */
    private int inputTypingDelay = 450;
    private boolean inputDelayDisabled = false;

    public RenderingEnv(ActionController actionController) {
        userActionInterceptor = new ViewUserActionInterceptor(actionController);
        currentFormContexts = new ArrayList<>();
    }

    /**
     * Clears composite context before starting the rendering process
     */
    public void initialize() {
        if (this.globalContext == null) {
            throw new IllegalStateException(DevConsole.error("Global Context is not set, call " +
                    "setGlobaContext first!!."));
        }
        this.combinedContext = null;
        // remove last context from global context
        if (!currentFormContexts.isEmpty()) {
            for (FormContext formContext : currentFormContexts) {
                this.globalContext.removeContext(formContext);
            }
        }
        currentFormContexts.clear();
    }

    public CompositeContext getContext() {
        return (combinedContext == null) ? globalContext : combinedContext;
    }

    public void disableInterceptors() {
        this.userActionInterceptor.setDisabled(true);
    }

    public void enableInterceptors() {
        this.userActionInterceptor.setDisabled(false);
    }

    public FormContext getFormContext() {
        return formContext;
    }

    /**
     * Every time a form rendering starts, the RenderEnv stores the context and links it with
     * the formId to the global context. So relative access can be done inside the form elements,
     * but also absolute access inter-form can be achieve throught the global context references.
     *
     * @param formContext
     */
    public void setFormContext(FormContext formContext) {
        if (formContext == null) {
            throw new IllegalStateException("FormContext mustn't be null!.");
        }
        this.formContext = formContext;
        // add form to context with full id
        this.globalContext.addContext(formContext);
        // register
        this.combinedContext = ContextUtils.combine(globalContext, formContext);
        // register this FormContext
        currentFormContexts.add(formContext);
    }

    public ViewUserActionInterceptor getUserActionInterceptor() {
        return userActionInterceptor;
    }


    public void addDeferred(String componentId, DeferredView view) {
        if (this.deferredViews == null) {
            this.deferredViews = new HashMap<>();
        }
        this.deferredViews.put(componentId, view);
    }

    public Map<String, DeferredView> getDeferredViews() {
        return deferredViews;
    }

    public void setViewDAG(ViewDAG viewDAG) {
        this.viewDAG = viewDAG;
    }

    public ViewDAG getViewDAG() {
        return viewDAG;
    }

    public void setViewContext(Context viewContext) {
        this.viewContext = viewContext;
    }

    public Context getViewContext() {
        return viewContext;
    }

    public void setViewRoot(View view) {
        this.viewRoot = view;
    }

    public View getViewRoot() {
        return viewRoot;
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
}

