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

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.actions.events.UserEventInterceptor;
import es.jcyl.ita.formic.forms.components.form.WidgetContextHolder;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.context.impl.EntityContext;
import es.jcyl.ita.formic.forms.view.activities.FormActivity;
import es.jcyl.ita.formic.forms.view.dag.ViewDAG;
import es.jcyl.ita.formic.forms.view.render.DeferredView;
import es.jcyl.ita.formic.forms.view.selection.SelectionManager;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHelper;
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
    private CompositeContext globalContext;
    private WidgetContext widgetContext;
    /**
     * Wrapper for globalContext, used in case contxt is accesed before first WidgetContextHolder
     * has been renderer. Testing purposes mainly.
     */
    private static WidgetContext EMPTY_WIDGET_CTX;
    private ViewWidget rootWidget;

    private SelectionManager selectionManager = new SelectionManager();
    /**
     * view rendering
     */
    private ViewDAG viewDAG;
    private Map<String, DeferredView> deferredViews;
    private UserEventInterceptor userActionInterceptor;
    private Context viewContext; // current view Android Context
    private FormActivity formActivity;
    /**
     * User text typing delay controls
     */
    private int inputTypingDelay = 450;
    private boolean inputDelayDisabled = false;
    // current entity in context
    private Entity entity;

    public RenderingEnv(ActionController actionController) {
        userActionInterceptor = new UserEventInterceptor(actionController);
    }

    protected RenderingEnv(){
    }

    public static RenderingEnv clone(RenderingEnv env){
        RenderingEnv newEnv = new RenderingEnv();
        newEnv.globalContext = env.globalContext;
        newEnv.widgetContext = env.widgetContext;
        newEnv.rootWidget = env.rootWidget;
        newEnv.viewDAG = env.viewDAG;
        newEnv.deferredViews = env.deferredViews;
        newEnv.userActionInterceptor = env.userActionInterceptor;
        newEnv.viewContext = env.viewContext;
        newEnv.formActivity = env.formActivity;
        newEnv.inputTypingDelay = env.inputTypingDelay;
        newEnv.inputDelayDisabled = env.inputDelayDisabled;
        newEnv.entity = env.entity;
        return newEnv;
    }

    /**
     * Clears composite context before starting the rendering process
     */
    public void initialize() {
        if (this.globalContext == null) {
            throw new IllegalStateException(DevConsole.error("Global Context is not set, call " +
                    "setGlobalContext first!!."));
        }
        this.clearSelection();
        this.clearMessages();
        this.initEmptyWidgetCtx(globalContext);
    }

    public void clearMessages() {
        WidgetContextHelper.clearMessages(this.globalContext);
    }

    CompositeContext getContext() {
        return globalContext;
    }

    public void disableInterceptors() {
        this.userActionInterceptor.setDisabled(true);
    }

    public void enableInterceptors() {
        this.userActionInterceptor.setDisabled(false);
    }

    public WidgetContext getWidgetContext() {
        return (widgetContext == null) ? EMPTY_WIDGET_CTX : widgetContext;
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
        this.globalContext.addContext(new BasicContext("messages"));
        initEmptyWidgetCtx(globalContext);
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
        if(this.getWidgetContext() == EMPTY_WIDGET_CTX){
            EMPTY_WIDGET_CTX.setEntity(entity);
        }
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


    private void initEmptyWidgetCtx(CompositeContext gContxt) {

        EMPTY_WIDGET_CTX = new WidgetContext(new WidgetContextHolder() {
            @Override
            public String getHolderId() {
                return null;
            }

            @Override
            public Widget getWidget() {
                return null;
            }

            @Override
            public WidgetContext getWidgetContext() {
                return null;
            }

            @Override
            public void setWidgetContext(WidgetContext context) {

            }
        });
        EMPTY_WIDGET_CTX.addContext(gContxt);
    }

}

