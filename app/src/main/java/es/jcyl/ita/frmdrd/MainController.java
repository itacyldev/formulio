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
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.context.Context;
import es.jcyl.ita.crtrepo.context.impl.BasicContext;
import es.jcyl.ita.crtrepo.context.impl.OrderedCompositeContext;
import es.jcyl.ita.frmdrd.configuration.FormConfigHandler;
import es.jcyl.ita.frmdrd.context.impl.DateTimeContext;
import es.jcyl.ita.frmdrd.forms.FormController;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;
import es.jcyl.ita.frmdrd.ui.validation.ValidationException;
import es.jcyl.ita.frmdrd.view.FormEditViewHandlerActivity;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class MainController {

    private static MainController _instance;
    private static UIView viewRoot;
    private static FormController formController;
    private static CompositeContext globalContext;

    public static MainController getInstance() {
        if (_instance == null) {
            _instance = new MainController();
        }
        return _instance;
    }

    private MainController() {
        globalContext = new OrderedCompositeContext();
        globalContext.addContext(new DateTimeContext("date"));
    }

    public void navigate(android.content.Context context, String viewId) {
        navigate(context, viewId, new HashMap<>());
    }

    public void navigate(android.content.Context context, String viewId, Object entityId) {
        Map<String, Serializable> params = new HashMap<>();
        params.put("entityId", (Serializable) entityId);
        navigate(context, viewId, params);
    }

    /**
     * Implements navigation to a new view
     *
     * @param andContext
     * @param formId: form configuration to load
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
        viewRoot = formController.getEditView();
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


    public void doUserAction(UIComponent component) {

        // retrieve current form's context
        UIForm form = (UIForm) component.getParentForm();

        // validation action
        try {
            component.validate(form.getContext());
            // trigger parent validation??
        } catch (ValidationException e) {
            // show message and return to view
        }
        // IF the field is mapped with and entity field, apply changes on the entity
        // update the main entity for this form using the context and fire-up and reactive flow
        form.applyChanges();
    }


    /**
     * Method called from the reactor engine to notify the view which components have to be updated
     */
    public void updateView() {

    }

    public void doSave() {
        formController.save();
    }

    public UIComponent getViewRoot() {
        return viewRoot;
    }

    public CompositeContext getGlobalContext() {
        return globalContext;
    }

    public static FormController getFormController() {
        return formController;
    }

}
