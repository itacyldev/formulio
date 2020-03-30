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


import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.context.impl.BasicContext;
import es.jcyl.ita.crtrepo.context.impl.OrderedCompositeContext;
import es.jcyl.ita.frmdrd.configuration.FormConfigHandler;
import es.jcyl.ita.frmdrd.context.impl.DateTimeContext;
import es.jcyl.ita.frmdrd.context.impl.FormViewContext;
import es.jcyl.ita.frmdrd.forms.FormController;
import es.jcyl.ita.frmdrd.render.ExecEnvironment;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;
import es.jcyl.ita.frmdrd.ui.validation.ValidatorException;
import es.jcyl.ita.frmdrd.view.FormEditViewHandlerActivity;
import es.jcyl.ita.frmdrd.view.ViewRenderHelper;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class MainController {

    private static MainController _instance;
    // configuration componentes and android components
    private UIView uiView;
    private View viewRoot;
    // android context and formContext
    private android.content.Context viewContext;
    private CompositeContext globalContext;
    // current form controller
    private FormController formController;
    private ViewRenderHelper renderHelper = new ViewRenderHelper();
    private ExecEnvironment execEnvironment;

    public static MainController getInstance() {
        if (_instance == null) {
            _instance = new MainController();
        }
        return _instance;
    }

    private MainController() {
        globalContext = new OrderedCompositeContext();
        globalContext.addContext(new DateTimeContext("date"));

        execEnvironment = new ExecEnvironment(globalContext);
    }

    public void navigate(android.content.Context context, String formId) {
        navigate(context, formId, new HashMap<>());
    }

    public void navigate(android.content.Context context, String viewId, Object entityId) {
        Map<String, Serializable> params = new HashMap<>();
        params.put("entityId", (Serializable) entityId);
        navigate(context, viewId, params);
        // new Execution environmnent
    }

    /**
     * Implements navigation to a new view
     *
     * @param andContext
     * @param formId:    form configuration to load
     * @param params
     */
    public void navigate(android.content.Context andContext, String formId,
                         @Nullable Map<String, Serializable> params) {

        // remove last view context and create a new one for the starting view
        globalContext.removeContext("view");
        CompositeContext viewCtx = prepareViewContext(params);
        globalContext.addContext(viewCtx);

        // get form configuration for given formId and load data
        formController = retrieveForm(formId);
        formController.load(globalContext);

        // set form view as current
        uiView = formController.getEditView();
        // Start activity
        final Intent intent = new Intent(andContext, FormEditViewHandlerActivity.class);
        andContext.startActivity(intent);
    }

    private FormController retrieveForm(String formId) {
        return FormConfigHandler.getForm(formId);
    }

    private CompositeContext prepareViewContext(@Nullable Map<String, Serializable> params) {
        CompositeContext viewCtx = new OrderedCompositeContext();
        viewCtx.setPrefix("view");
        BasicContext pContext = new BasicContext("params");
        pContext.putAll(params);
        viewCtx.addContext(pContext);
        return viewCtx;
    }

    /**
     * Updates the entity property linked with the component.
     * If a validation error occurs the component view is re-rendered
     *
     * @param component
     */
    public void doUserAction(UIComponent component) {
        try {
            formController.updateEntity((UIField) component);
        } catch (ValidatorException e) {
            updateView(component);
        }
    }

    /**
     * Method called from the reactor engine to notify the view which components have to be updated
     */
    public void updateView(UIComponent component) {
        // find related view element
        UIForm form = component.getParentForm();
        // find view using viewContext
        FormViewContext viewContext = form.getContext().getViewContext();
        View view = viewContext.findComponentView(component.getId());
        // re-render view
        View newView = renderHelper.render(this.viewContext, this.execEnvironment, component);
        renderHelper.replaceView(view, newView);
    }

    public void doSave() {
        try{
            formController.save();
        }catch (ValidatorException e){
            // re-render all the screen
            View newView = renderHelper.render(this.viewContext, this.execEnvironment, formController.getEditView());
            renderHelper.replaceView(viewRoot, newView);
        }
    }

    public UIView getViewRoot() {
        return uiView;
    }

    public CompositeContext getGlobalContext() {
        return globalContext;
    }

    public FormController getFormController() {
        return formController;
    }

    public ExecEnvironment getExecEnvironment() {
        return execEnvironment;
    }

    public void setViewContext(android.content.Context viewContext) {
        this.viewContext = viewContext;
    }

    public void setViewRoot(View viewRoot) {
        this.viewRoot = viewRoot;
    }
}
