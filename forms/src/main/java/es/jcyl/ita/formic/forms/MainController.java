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
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.android.dx.stock.ProxyBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.ContextAwareComponent;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.controllers.FormException;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.controllers.ViewControllerFactory;
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
    private ViewController viewController;
    private ViewControllerFactory formControllerFactory;
    private ReactivityFlowManager flowManager;
    private ScriptEngine scriptEngine;

    // navigation control
    private Router router;
    private static HashMap<Class, Class> staticMap;
    private MCState state;


    public static MainController getInstance() {
        if (_instance == null) {
            _instance = new MainController();
        }
        return _instance;
    }

    MainController() {
        formControllerFactory = ViewControllerFactory.getInstance();
        formControllerFactory.setMc(this);
        router = new Router(this);
        actionController = new ActionController(this, router);
        renderingEnv = new RenderingEnv(actionController);
        scriptEngine = ScriptEngine.getInstance();
//        flowManager = ReactivityFlowManager.getInstance();
        registerFormTypeViews();
    }
    private void initProxy(){
        InvocationHandler handler = buildInvocationHandler();

        try {
            File dx = Config.getInstance().getAndroidContext().getDir("dx", Context.MODE_PRIVATE);
            Random debugRandom = ProxyBuilder.forClass(Random.class)
                    .dexCache(dx)
                    .handler(handler)
                    .build();
            int i = debugRandom.nextInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InvocationHandler buildInvocationHandler() {
        InvocationHandler handler = new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("nextInt")) {
                    // Chosen by fair dice roll, guaranteed to be random.
                    return 4;
                }
                Object result = ProxyBuilder.callSuper(proxy, method, args);
                System.out.println("Method: " + method.getName() + " args: "
                        + Arrays.toString(args) + " result: " + result);
                return result;
            }
        };
        return handler;
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
                         Map<String, Object> params) {
        initProxy();
        saveMCState();

        setupParamsContext(params);
        try {
            // get form configuration for given formId and load data
            ViewController controller = getViewController(formId);
            this.viewController = controller;
            this.viewController.load(globalContext);
            this.scriptEngine.initScope(controller.getId());
        } catch (Exception e) {
            restoreMCState();
            throw e;
        }

        // get the activity class for current controller
        initActivity(andContext);
    }

    protected ViewController getViewController(String formId) {
        ViewController controller = formControllerFactory.getController(formId);
        if (controller == null) {
            throw new FormException(error(String.format("No form controller found with id [%s] " +
                            "check route string. Available ids: %s", formId,
                    new TreeSet<>(formControllerFactory.getControllerIds()))));
        }
        return controller;
    }

    protected void initActivity(android.content.Context context) {
        Class activityClazz = getViewImpl(viewController);
        // Start activity to get Android context
        Intent intent = new Intent(context, activityClazz);
        context.startActivity(intent);
    }

    private void saveMCState() {
        if (this.viewController != null) {
            this.state = new MCState(this.viewController, globalContext.getContext("params"));
        }
    }

    private void restoreMCState() {
        if (state != null) {
            this.viewController = this.state.fc;
            globalContext.addContext(this.state.params);
            this.viewController.load(globalContext);
        }
    }


    private void setupParamsContext(@Nullable Map<String, Object> params) {
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
        formActivity.setViewController(this.viewController);
        // set the View elements to controllers
        this.router.registerActivity(formActivity.getActivity());
        this.viewController.setContentView(formActivity.getContentView());
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
        staticMap.put(ViewController.class, FormEditViewHandlerActivity.class);
        staticMap.put(FormListController.class, FormListViewHandlerActivity.class);
        // TODO: dynamic mapping for extensions

    }

    protected Class getViewImpl(ViewController formController) {
        return staticMap.get(formController.getClass());
    }

    /**
     * Renders current view in the given Android context and returns the generated View
     *
     * @param viewContext: Android context
     * @return
     */
    public Widget renderView(Context viewContext) {
        UIView uiView = viewController.getView();
        ViewDAG viewDAG = DAGManager.getInstance().getViewDAG(uiView.getId());

        renderingEnv.initialize();
        renderingEnv.setAndroidContext(viewContext);
        renderingEnv.setViewDAG(viewDAG);
        renderingEnv.setScriptEngine(scriptEngine);
        renderingEnv.disableInterceptors();

        // render view Widget and restore partial state if needed
        viewController.onBeforeRender();
        Widget widget = viewRenderer.render(renderingEnv, uiView);
        viewController.setRootWidget((ViewWidget) widget);
        restorePrevState(viewController);

        renderingEnv.enableInterceptors();
        viewController.onAfterRender(widget); //TODO: after or before enabling Interceptors?

        return widget;
    }

    public Widget renderComponent(UIComponent component) {
        renderingEnv.disableInterceptors();
        Widget widget = viewRenderer.render(this.renderingEnv, component);
        renderingEnv.enableInterceptors();
        return widget;
    }

    /**
     * Restore view previous state if needed
     *
     * @param formController
     */
    private void restorePrevState(ViewController formController) {
        UserAction action = actionController.getCurrentAction();
        if (action != null && action.isRestoreView()) {
            formController.restoreViewPartialState();
        }
    }

    /**
     * Method called from the reactor engine to notify the vew which components have to be updated
     */
    public Widget updateView(Widget widget) {
        return updateView(widget, false);
    }

    public Widget updateView(Widget widget, boolean reactiveCall) {
        // render the new Android view for the component and replace it
        renderingEnv.disableInterceptors();
        Widget newView = viewRenderer.render(this.renderingEnv, widget.getComponent());
        viewRenderer.replaceView(widget, newView);
        renderingEnv.enableInterceptors();
        return newView;
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
     * Re-renders last view to show validation errors or updated entity values.
     */
    public void renderBack() {
        // render again the form to show validation error
        renderingEnv.disableInterceptors();
        try {
            Widget newRootWidget = viewRenderer.render(renderingEnv, viewController.getView());
            // the View elements to replace hang from the content view of the formController
            ViewGroup contentView = viewController.getContentView();
            contentView.removeAllViews();
            contentView.addView(newRootWidget);
            getViewController().setRootWidget((ViewWidget) newRootWidget);
        } finally {
            renderingEnv.enableInterceptors();
        }
    }

    public void saveViewState() {
        getViewController().saveViewState();
    }

    /**
     * Disables user events and restore values to the view
     */
    public void restoreViewState() {
        renderingEnv.disableInterceptors();
        try {
            viewController.restoreViewState();
        } finally {
            renderingEnv.enableInterceptors();
        }
    }

    public class MCState {
        ViewController fc;
        es.jcyl.ita.formic.core.context.Context params;

        public MCState(ViewController fc, es.jcyl.ita.formic.core.context.Context params) {
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

    public ViewController getViewController() {
        return viewController;
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

    @Override
    public void setContext(es.jcyl.ita.formic.core.context.Context ctx) {
        if (!(ctx instanceof CompositeContext)) {
            throw new IllegalArgumentException(error("MainController needs an instance of CompositeContext to use it as GlobalContext."));
        }
        this.globalContext = (CompositeContext) ctx; // global context is received
        renderingEnv.setGlobalContext(this.globalContext);
        setupScriptingEnv(globalContext);
        // add state and message context
        globalContext.addContext(new BasicContext("state"));// TODO: sobra?
    }

    /*** TODO: Just For Testing purposes until we setup dagger for Dep. injection**/
    public void setViewController(ViewController viewController) {
        this.viewController = viewController;
    }
}
