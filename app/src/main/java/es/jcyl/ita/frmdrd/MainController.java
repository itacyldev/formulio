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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import es.jcyl.ita.frmdrd.configuration.FormConfigHandler;
import es.jcyl.ita.frmdrd.context.CompositeContext;
import es.jcyl.ita.frmdrd.context.Context;
import es.jcyl.ita.frmdrd.context.OrderedCompositeContext;
import es.jcyl.ita.frmdrd.context.impl.BasicContext;
import es.jcyl.ita.frmdrd.context.impl.DateTimeContext;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;
import es.jcyl.ita.frmdrd.ui.validation.ValidationException;
import es.jcyl.ita.frmdrd.view.FormViewHandlerActivity;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class MainController {

    private static MainController _instance;
    private static UIView viewRoot;
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
     * @param viewId
     * @param params
     */
    public void navigate(android.content.Context andContext,
                         String viewId, @Nullable Map<String, Serializable> params) {
        // remove last view context and create a new one for the starting view
        globalContext.removeContext("view");
        CompositeContext viewCtx = prepareViewContext(params);
        globalContext.addContext(viewCtx);

        // read view config and configure it if needed
        viewRoot = retrieveView(viewId);
        // for each form in the view, load the related entity
        for (UIForm form : viewRoot.getForms()) {
            form.initContext();
            form.loadEntity(globalContext);
            // get form context and attach them to view context
            Context fCtx = form.getContext();
            viewCtx.addContext(fCtx);
        }
        // Start activity
        final Intent intent = new Intent(andContext, FormViewHandlerActivity.class);
        andContext.startActivity(intent);
    }

    private CompositeContext prepareViewContext(@Nullable Map<String, Serializable> params) {
        CompositeContext viewCtx = new OrderedCompositeContext();
        BasicContext pContext = new BasicContext("params");
        pContext.putAll(params);
        viewCtx.addContext(pContext);
        return viewCtx;
    }

    private UIView retrieveView(String viewId) {
        // find view in repository and inflate it from xml configuration if it already doesn't exits
        return FormConfigHandler.getView(viewId);
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
     * Finds the proper context for the given form
     *
     * @param formId
     * @return
     */
    private Context retrieveFormContext(UIForm formId) {
        return null;
    }

    /**
     * Method called from the reactor engine to notify the view which components have to be updated
     */
    public void updateView() {

    }

    private void applyChanges(Context formContext) {

    }

    public void doSave() {
        // get form's related entity
        // get entity's related repository
        // persist entity
    }

    private void updateContext(String componentId) {
        // do we have to update the context from the user input or let the components validate just using form.elementId.value
    }

    public UIComponent getViewRoot() {
        return viewRoot;
    }

    public CompositeContext getGlobalContext() {
        return globalContext;
    }
}
