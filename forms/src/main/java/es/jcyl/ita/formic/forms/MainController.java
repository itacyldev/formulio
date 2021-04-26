package es.jcyl.ita.formic.forms;
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


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.ContextAwareComponent;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.controllers.FormController;
import es.jcyl.ita.formic.forms.controllers.FormControllerFactory;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.controllers.FormException;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.forms.reactivity.ReactivityFlowManager;
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.forms.scripts.RhinoViewRenderHandler;
import es.jcyl.ita.formic.forms.scripts.ScriptEngine;
import es.jcyl.ita.formic.forms.view.activities.FormActivity;
import es.jcyl.ita.formic.forms.view.activities.FormEditViewHandlerActivity;
import es.jcyl.ita.formic.forms.view.activities.FormListViewHandlerActivity;
import es.jcyl.ita.formic.forms.view.dag.DAGManager;
import es.jcyl.ita.formic.forms.view.dag.ViewDAG;
import es.jcyl.ita.formic.forms.view.render.ViewRendererEventHandler;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.renderer.ViewRenderer;
import es.jcyl.ita.formic.forms.view.widget.Widget;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Controls navigation between forms and the main loading and rendering flow.
 * <p>
 * Shares the context, AndroidContext and current components and view data with action handlers
 * to implement user actions.
 */

public class MainController implements ContextAwareComponent {

    private static MainController _instance;

    // Global context and root component
    private CompositeContext globalContext;

    // helper to render the uiView in an Android Context
    private ViewRenderer viewRenderer = new ViewRenderer();
    private RenderingEnv renderingEnv;

    // user action management
    private ActionController actionController;
    private FormController formController;
    private FormControllerFactory formControllerFactory;
    private ReactivityFlowManager flowManager;
    private ScriptEngine scriptEngine;

    // navigation control
    private Router router;
    private static HashMap<Class, Class> staticMap;
    private State state;


    public static MainController getInstance() {
        if (_instance == null) {
            _instance = new MainController();
        }
        return _instance;
    }

    MainController() {
        formControllerFactory = FormControllerFactory.getInstance();
        formControllerFactory.setMc(this);
        router = new Router(this);
        actionController = new ActionController(this, router);
        renderingEnv = new RenderingEnv(actionController);
        scriptEngine = ScriptEngine.getInstance();
        flowManager = ReactivityFlowManager.getInstance();
        registerFormTypeViews();
    }

    private void setupScriptingEnv(es.jcyl.ita.formic.core.context.Context ctx) {
        Map<String, Object> props = new HashMap<>();
        props.put("ctx", ctx);
        props.put("renderEnv", this.renderingEnv);
        props.put("console", new DevConsole());
        scriptEngine.initEngine(props);
        // add event handler to execute scripts during component rendering
        ViewRendererEventHandler handler = new RhinoViewRenderHandler(scriptEngine);
        this.viewRenderer.setEventHandler(handler);
    }


    /*********************************************/
    /***  Navigation control methods */
    /*********************************************/

    /**
     * Implements navigation to a new view
     *
     * @param andContext
     * @param formId
     * @param params
     */
    public void navigate(android.content.Context andContext, String formId,
                         Map<String, Serializable> params) {
        saveState();

        setupParamsContext(params);
        try {
            // get form configuration for given formId and load data
            FormController controller = formControllerFactory.getController(formId);
            if (controller == null) {
                throw new FormException(error(String.format("No form controller found with id [%s] " +
                                "check route string. Available ids: %s", formId,
                        formControllerFactory.getControllerIds())));
            }
            this.formController = controller;
            this.formController.load(globalContext);
            this.scriptEngine.initScope();
        } catch (Exception e) {
            restoreState();
            throw e;
        }

        // get the activity class for current controller
        initActivity(andContext);
    }

    protected void initActivity(android.content.Context context) {
        Class activityClazz = getViewImpl(formController);
        // Start activity to get Android context
        Intent intent = new Intent(context, activityClazz);
        context.startActivity(intent);
    }

    private void saveState() {
        if (this.formController != null) {
            this.state = new State(this.formController, globalContext.getContext("params"));
        }
    }

    private void restoreState() {
        if (state != null) {
            this.formController = this.state.fc;
            globalContext.addContext(this.state.params);
            this.formController.load(globalContext);
        }
    }


    private void setupParamsContext(@Nullable Map<String, Serializable> params) {
        BasicContext pContext = new BasicContext("params");
        if (params != null) {
            pContext.putAll(params);
        }
        globalContext.addContext(pContext);
    }

    public void registerActivity(FormActivity formActivity) {
        // setups the activity during initialization to get reference to rendering and actions objects
        formActivity.setRenderingEnv(this.renderingEnv);
        formActivity.setRouter(this.router);
        formActivity.setFormController(this.formController);
        // set the View elements to controllers
        this.router.registerActivity(formActivity.getActivity());
        this.formController.setContentView(formActivity.getContentView());
        this.renderingEnv.setFormActivity(formActivity);
    }


    /*********************************************/
    /***  View rendering methods */
    /*********************************************/
    /**
     * static mapping to relate each form type with the implenting view.
     */
    private void registerFormTypeViews() {
        staticMap = new HashMap<>(2);
        staticMap.put(FormEditController.class, FormEditViewHandlerActivity.class);
        staticMap.put(FormListController.class, FormListViewHandlerActivity.class);
        // TODO: dynamic mapping for extensions

    }

    protected Class getViewImpl(FormController formController) {
        return staticMap.get(formController.getClass());
    }

    /**
     * Renders current view in the given Android context and returns the generated View
     *
     * @param viewContext: Android context
     * @return
     */
    public Widget renderView(Context viewContext) {
        UIView uiView = this.formController.getView();
        ViewDAG viewDAG = DAGManager.getInstance().getViewDAG(uiView.getId());

        renderingEnv.initialize();
        renderingEnv.setAndroidContext(viewContext);
        renderingEnv.setViewDAG(viewDAG);
        renderingEnv.disableInterceptors();

        formController.onBeforeRender();
        Widget widget = viewRenderer.render(renderingEnv, uiView);
        formController.setRootWidget((ViewWidget) widget);
        renderingEnv.enableInterceptors();

        formController.onAfterRender(widget);
        return widget;
    }

    /**
     * Method called from the reactor engine to notify the vew which components have to be updated
     */
    public Widget updateView(Widget widget, boolean reactiveCall) {
        // render the new Android view for the component and replace it
        renderingEnv.disableInterceptors();
        Widget newView = viewRenderer.render(this.renderingEnv, widget.getComponent());
        viewRenderer.replaceView(widget, newView);
        renderingEnv.enableInterceptors();
        return newView;
//        if (!reactiveCall) {
//            flowManager.execute(component.getAbsoluteId());
//        }
    }

    /**
     * Renders all the views with expressions that depend on given element.
     *
     * @param widget: ui component that fires the changes in the View.
     */
    public void updateDependants(Widget widget) {
        viewRenderer.renderDeps(this.renderingEnv, widget);
    }

    /**
     * Re-renders last view to show validation errors
     */
    public void renderBack() {
        // render again the form to show validation error
        renderingEnv.disableInterceptors();
        try {
            Widget newRootWidget = viewRenderer.render(renderingEnv, formController.getView());

            // the View elements to replace hang from the content view of the formController
            ViewGroup contentView = formController.getContentView();
            contentView.removeAllViews();
            contentView.addView(newRootWidget);
            formController.setRootWidget((ViewWidget) newRootWidget);

            // disable user events and restore values to the view
            formController.restoreViewState();
        } finally {
            renderingEnv.enableInterceptors();
        }
    }


    public class State {
        FormController fc;
        es.jcyl.ita.formic.core.context.Context params;

        public State(FormController fc, es.jcyl.ita.formic.core.context.Context params) {
            this.fc = fc;
            this.params = params;
        }
    }
    /*********************************************/
    /***  GETTERS TO ACCESS SHARED INFORMATION */
    /*********************************************/

    public CompositeContext getGlobalContext() {
        return globalContext;
    }

    public FormController getFormController() {
        return formController;
    }

    public RenderingEnv getRenderingEnv() {
        return renderingEnv;
    }

    public ActionController getActionController() {
        return actionController;
    }

    public Router getRouter() {
        return router;
    }

    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    /*** TODO: Just For Testing purposes until we setup dagger for Dep. injection**/
    public void setFormController(FormController fc, UIView view) {
        this.formController = fc;
    }

    @Override
    public void setContext(es.jcyl.ita.formic.core.context.Context ctx) {
        if (!(ctx instanceof CompositeContext)) {
            throw new IllegalArgumentException(error("MainController needs an instance of CompositeContext to use it as GlobalContext."));
        }
        this.globalContext = (CompositeContext) ctx; // global context is received
        renderingEnv.setGlobalContext(this.globalContext);
        setupScriptingEnv(globalContext);
        // add state and message context
        globalContext.addContext(new BasicContext("messages"));
        globalContext.addContext(new BasicContext("state"));
    }
}
