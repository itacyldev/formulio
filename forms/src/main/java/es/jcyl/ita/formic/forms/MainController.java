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
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.context.impl.FormViewContext;
import es.jcyl.ita.formic.forms.controllers.FormController;
import es.jcyl.ita.formic.forms.controllers.FormControllerFactory;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.forms.reactivity.ReactivityFlowManager;
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.forms.view.activities.FormActivity;
import es.jcyl.ita.formic.forms.view.activities.FormEditViewHandlerActivity;
import es.jcyl.ita.formic.forms.view.activities.FormListViewHandlerActivity;
import es.jcyl.ita.formic.forms.view.dag.DAGManager;
import es.jcyl.ita.formic.forms.view.dag.ViewDAG;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.ViewRenderHelper;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;

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
    private ViewRenderHelper renderHelper = new ViewRenderHelper();
    private RenderingEnv renderingEnv;

    // user action management
    private ActionController actionController;
    private FormController formController;
    private FormControllerFactory formControllerFactory;
    private ReactivityFlowManager flowManager;

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

    private MainController() {
        formControllerFactory = FormControllerFactory.getInstance();
        router = new Router(this);
        actionController = new ActionController(this, router);
        flowManager = ReactivityFlowManager.getInstance();
        registerFormTypeViews();
    }

    private void init(CompositeContext context) {
        globalContext = context;
        renderingEnv = new RenderingEnv(globalContext, actionController);
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
                         @Nullable Map<String, Serializable> params) {
        saveState();

        setupParamsContext(params);
        try {
            // get form configuration for given formId and load data
            this.formController = formControllerFactory.getController(formId);
            this.formController.load(globalContext);
        } catch (Exception e) {
            restoreState();
            throw e;
        }

        // get the activity class for current controller
        Class activityClazz = getViewImpl(formController);

        // Start activity to get Android context
        Intent intent = new Intent(andContext, activityClazz);
        andContext.startActivity(intent);
    }

    private void saveState() {
        this.state = new State(this.formController, globalContext.getContext("params"));
    }

    private void restoreState() {
        this.formController = this.state.fc;
        globalContext.addContext(this.state.params);
        this.formController.load(globalContext);
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

    private Class getViewImpl(FormController formController) {
        return staticMap.get(formController.getClass());
    }

    /**
     * Renders current view in the given Android context and returns the generated View
     *
     * @param viewContext: Android context
     * @return
     */
    public View renderView(Context viewContext) {
        UIView uiView = this.formController.getView();
        ViewDAG viewDAG = DAGManager.getInstance().getViewDAG(uiView.getId());

        renderingEnv.initialize();
        renderingEnv.setViewContext(viewContext);
        renderingEnv.setViewDAG(viewDAG);
        renderingEnv.disableInterceptors();
        View view = renderHelper.render(renderingEnv, uiView);
        renderingEnv.enableInterceptors();
        // set the root View element to renderingEnv for re-renders in the same view
        renderingEnv.setViewRoot(view);
        return view;
    }

    /**
     * Method called from the reactor engine to notify the vew which components have to be updated
     */
    public void updateView(UIComponent component, boolean reactiveCall) {
        // find related view element
        UIForm form = component.getParentForm();
        // find view using viewContext
        FormViewContext viewContext = form.getContext().getViewContext();

        InputWidget fieldView = viewContext.findInputFieldViewById(component.getId());
        // render the new Android view for the component and replace it
        renderingEnv.disableInterceptors();
        View newView = renderHelper.render(this.renderingEnv, component);
        renderingEnv.enableInterceptors();
        renderHelper.replaceView(fieldView, newView);

        if (!reactiveCall) {
            flowManager.execute(component.getAbsoluteId());
        }
    }

    /**
     * Renders all the views with expressions that depend on given element.
     *
     * @param component: ui component that fires the changes in the View.
     */
    public void updateDependants(UIComponent component) {
        renderHelper.renderDeps(this.renderingEnv, component);
    }

    /**
     * Re-renders last view to show validation errors
     */
    public void renderBack() {
        // render again the form to show validation error
        renderingEnv.disableInterceptors();
        View newView = renderHelper.render(renderingEnv, formController.getView());
        renderingEnv.enableInterceptors();

        // the View elements to replace hang from the content view of the formController
        ViewGroup contentView = formController.getContentView();
        contentView.removeAllViews();
        contentView.addView(newView);

        // disable user events and restore values to the view
        renderingEnv.disableInterceptors();
        formController.restoreViewState();
        renderingEnv.enableInterceptors();
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

    /*** TODO: Just For Testing purposes until we setup dagger for Dep. injection**/
    public void setFormController(FormController fc, UIView view) {
        this.formController = fc;
    }

    @Override
    public void setContext(es.jcyl.ita.formic.core.context.Context ctx) {
        if (!(ctx instanceof CompositeContext)) {
            throw new IllegalArgumentException(DevConsole.error("MainController needs an instance of CompositeContext to use it as GlobalContext."));
        }
        this.globalContext = (CompositeContext) ctx; // global context is received
        this.init(globalContext);
    }


}
