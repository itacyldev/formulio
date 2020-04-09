package es.jcyl.ita.frmdrd;
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
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.context.impl.BasicContext;
import es.jcyl.ita.frmdrd.actions.ActionController;
import es.jcyl.ita.frmdrd.context.impl.DateTimeContext;
import es.jcyl.ita.frmdrd.context.impl.FormViewContext;
import es.jcyl.ita.frmdrd.context.impl.UnPrefixedCompositeContext;
import es.jcyl.ita.frmdrd.forms.FormController;
import es.jcyl.ita.frmdrd.forms.FormControllerFactory;
import es.jcyl.ita.frmdrd.forms.FormEditController;
import es.jcyl.ita.frmdrd.forms.FormListController;
import es.jcyl.ita.frmdrd.reactivity.ReactivityFlowManager;
import es.jcyl.ita.frmdrd.router.Router;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;
import es.jcyl.ita.frmdrd.view.InputFieldView;
import es.jcyl.ita.frmdrd.view.activities.FormActivity;
import es.jcyl.ita.frmdrd.view.activities.FormEditViewHandlerActivity;
import es.jcyl.ita.frmdrd.view.activities.FormListViewHandlerActivity;
import es.jcyl.ita.frmdrd.view.dag.DAGManager;
import es.jcyl.ita.frmdrd.view.dag.ViewDAG;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;
import es.jcyl.ita.frmdrd.view.render.ViewRenderHelper;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Controls navigation between forms and the main loading and rendering flow.
 * <p>
 * Shares the context, AndroidContext and current components and view data with action handlers
 * to implement user actions.
 */

public class MainController {

    private static MainController _instance;

    // Android context, view and activity
    private android.content.Context viewContext;
    private View viewRoot;
    private Activity activity;

    // Global context and root component
    private CompositeContext globalContext;
    private UIView uiView;

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


    public static MainController getInstance() {
        if (_instance == null) {
            _instance = new MainController();
        }
        return _instance;
    }

    private MainController() {
        globalContext = new UnPrefixedCompositeContext();
        globalContext.addContext(new DateTimeContext("date"));
        formControllerFactory = FormControllerFactory.getInstance();
        router = new Router(this);
        actionController = new ActionController(this, router);
        renderingEnv = new RenderingEnv(globalContext, actionController);
        flowManager = ReactivityFlowManager.getInstance();
        registerFormTypeViews();
    }


    /*********************************************/
    /***  Navigation control methods */
    /*********************************************/
    /**
     * Implements navigation to a new view
     *
     * @param andContext
     * @param formId:    form configuration to load
     * @param params
     */
    public void navigate(android.content.Context andContext, String formId,
                         @Nullable Map<String, Serializable> params) {

        setupParamsContext(params);
        // get form configuration for given formId and load data
        formController = formControllerFactory.getController(formId);
        formController.load(globalContext);

        // set form view as current
        uiView = formController.getView();

        // get the activity class
        final Intent intent;
        Class activityClazz = getViewImpl(formController);
        intent = new Intent(andContext, activityClazz);
        // Start activity to get Android context
        andContext.startActivity(intent);
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
        this.activity = formActivity.getActivity();
        this.router.registerActivity(formActivity.getActivity());
        this.formController.setContentView(formActivity.getContentView());
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
        setViewContext(viewContext);
        renderingEnv.initialize();
        ViewDAG viewDAG = DAGManager.getInstance().getViewDAG(uiView.getId());
        renderingEnv.setViewDAG(viewDAG);
        this.viewRoot = renderHelper.render(viewContext, renderingEnv, this.uiView);
        // add references to form context directly from global context
//        globalContext.addAllContext(execEnvironment.getContextMap().getContexts());
        return this.viewRoot;
    }

    /**
     * Method called from the reactor engine to notify the view which components have to be updated
     */
    public void updateView(UIComponent component, boolean reactiveCall) {
        // find related view element
        UIForm form = component.getParentForm();
        // find view using viewContext
        FormViewContext viewContext = form.getContext().getViewContext();

        InputFieldView fieldView = viewContext.findInputFieldViewById(component.getId());
        // render the new Android view for the component and replace it
        View newView = renderHelper.render(this.viewContext, this.renderingEnv, component);
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
        renderHelper.renderDeps(this.viewContext, this.renderingEnv, component);
    }

    private void setViewContext(android.content.Context viewContext) {
        this.viewContext = viewContext;
    }


    /**
     * Re-renders last view to show validation errors
     */
    public void renderBack() {
        // render again the form to show validation error
        View newView = renderHelper.render(this.viewContext, renderingEnv, formController.getView());

        // the View elements to replace hang from the content view of the formController
        ViewGroup contentView = formController.getContentView();
        contentView.removeAllViews();
        contentView.addView(newView);

        // disable user events and restore values to the view
        renderingEnv.disableInterceptors();
        formController.restoreViewState();
        renderingEnv.enableInterceptors();
    }

    /*********************************************/
    /***  GETTERS TO ACCESS SHARED INFORMATION */
    /*********************************************/


    public Context getViewContext() {
        return viewContext;
    }

    public View getViewRoot() {
        return viewRoot;
    }

    public CompositeContext getGlobalContext() {
        return globalContext;
    }

    public UIView getUiView() {
        return uiView;
    }

    public FormController getFormController() {
        return formController;
    }

    public ViewRenderHelper getRenderHelper() {
        return renderHelper;
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

    /*** TODO: Just For Testing purposes until we setup dagger to Dep. injection**/
    public void setFormController(FormController fc, UIView view) {
        this.formController = fc;
        this.uiView = view;
    }

}
