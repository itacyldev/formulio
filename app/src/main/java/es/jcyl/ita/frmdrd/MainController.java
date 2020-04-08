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


import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Map;

import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.context.impl.BasicContext;
import es.jcyl.ita.frmdrd.actions.ActionController;
import es.jcyl.ita.frmdrd.configuration.FormConfigHandler;
import es.jcyl.ita.frmdrd.context.impl.DateTimeContext;
import es.jcyl.ita.frmdrd.context.impl.FormViewContext;
import es.jcyl.ita.frmdrd.context.impl.UnPrefixedCompositeContext;
import es.jcyl.ita.frmdrd.forms.FormController;
import es.jcyl.ita.frmdrd.reactivity.ReactivityFlowManager;
import es.jcyl.ita.frmdrd.router.Router;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;
import es.jcyl.ita.frmdrd.view.FormEditViewHandlerActivity;
import es.jcyl.ita.frmdrd.view.FormListViewHandlerActivity;
import es.jcyl.ita.frmdrd.view.InputFieldView;
import es.jcyl.ita.frmdrd.view.ViewRenderHelper;
import es.jcyl.ita.frmdrd.view.dag.DAGManager;
import es.jcyl.ita.frmdrd.view.dag.ViewDAG;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

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

    // Android context and view
    private android.content.Context viewContext;
    private View viewRoot;
    // Global context and root component
    private CompositeContext globalContext;
    private UIView uiView;

    // helper to render the uiView in an Android Context
    private ViewRenderHelper renderHelper = new ViewRenderHelper();
    private RenderingEnv renderingEnv;

    // user action management
    private ActionController actionController;
    private FormController formController;
    private ReactivityFlowManager flowManager;
    // navigation control
    private Router router;

    public static MainController getInstance() {
        if (_instance == null) {
            _instance = new MainController();
        }
        return _instance;
    }

    private MainController() {
        globalContext = new UnPrefixedCompositeContext();
        globalContext.addContext(new DateTimeContext("date"));
        actionController = new ActionController();
        renderingEnv = new RenderingEnv(globalContext, actionController);
        flowManager = ReactivityFlowManager.getInstance();
        router = new Router(this);
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
    public void navigate(android.content.Context andContext, String formId, String mode,
                         @Nullable Map<String, Serializable> params) {

        setupParamsContext(params);

        // get form configuration for given formId and load data
        formController = retrieveForm(formId);

        // set form view as current
        final Intent intent;
        // TODO: arggghhh
        if (mode.equalsIgnoreCase("list")) {
            uiView = formController.getListView();
            intent = new Intent(andContext, FormListViewHandlerActivity.class);
        } else {
            formController.load(globalContext);
            uiView = formController.getEditView();
//            DAGManager dagManager = new DAGManager();
//            dagManager.generateDags(uiView);
            intent = new Intent(andContext, FormEditViewHandlerActivity.class);
        }

        // Start activity to get Android context
        andContext.startActivity(intent);
    }

    private FormController retrieveForm(String formId) {
        FormController controller = FormConfigHandler.getForm(formId);
        if (controller == null) {
            throw new IllegalStateException(String.format("Invalid formId reference, no Form " +
                            "configuration found for id [%s]. Available forms in the project: %s.",
                    formId, FormConfigHandler.getAvailableFormIds()));
        }
        return controller;
    }

    private void setupParamsContext(@Nullable Map<String, Serializable> params) {
        BasicContext pContext = new BasicContext("params");
        if (params != null) {
            pContext.putAll(params);
        }
        globalContext.addContext(pContext);
    }

    /*********************************************/
    /***  View rendering methods */
    /*********************************************/
    /**
     * Renders current view in the given Android context and returns the generated View
     *
     * @param viewContext: Android context
     * @return
     */
    public View renderView(Context viewContext) {
        setViewContext(viewContext);
        renderingEnv.initialize();
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
     * Renders all the views with expressions that depend on given element
     *
     * @param component: ui component that fires the changes in the View
     */
    public void updateDependants(UIComponent component) {
        ViewDAG viewDAG = DAGManager.getInstance().getViewDAG(uiView.getId());
        renderHelper.renderDeps(this.viewContext, this.renderingEnv, viewDAG, component);
    }

    private void setViewContext(android.content.Context viewContext) {
        this.viewContext = viewContext;
        this.formController.setViewContext(viewContext);
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
