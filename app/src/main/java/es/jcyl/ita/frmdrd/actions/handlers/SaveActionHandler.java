package es.jcyl.ita.frmdrd.actions.handlers;
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

import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.frmdrd.MainController;
import es.jcyl.ita.frmdrd.actions.ActionHandler;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.actions.UserActionException;
import es.jcyl.ita.frmdrd.forms.FormEditController;
import es.jcyl.ita.frmdrd.router.Router;
import es.jcyl.ita.frmdrd.validation.ValidatorException;
import es.jcyl.ita.frmdrd.view.render.ViewRenderHelper;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class SaveActionHandler implements ActionHandler {

    @Override
    public void handle(UserAction action) {
        MainController mc = MainController.getInstance();

        if (!(mc.getFormController() instanceof FormEditController)) {
            throw new UserActionException(
                    "Save operations can be performed just in FormEditController forms.");
        }

        FormEditController formController = (FormEditController) mc.getFormController();
        // save view state for each form
        formController.saveViewState();

        try {
            formController.save();
            // stay or navigate back to list?
            if (StringUtils.isBlank(action.getRoute())) {
                Toast.makeText(action.getViewContext(),
                        "Entity successfully saved.", Toast.LENGTH_SHORT).show();
            } else {
                Router router = mc.getRouter();
                router.navigate(action.getViewContext(), action.getRoute(), action.getParams());
            }
        } catch (ValidatorException e) {
            // re-render all the screen
            ViewRenderHelper renderHelper = mc.getRenderHelper();
            View newView = renderHelper.render(mc.getViewContext(), mc.getRenderingEnv(),
                    formController.getView());
            // replace hole view
            ((FragmentActivity) mc.getViewContext()).setContentView(newView);
            mc.getRenderingEnv().disableInterceptors();
            formController.restoreViewState();
            mc.getRenderingEnv().enableInterceptors();

            Toast.makeText(action.getViewContext(), "The form is invalid, check your input.", Toast.LENGTH_SHORT).show();
        }

    }
}
