package es.jcyl.ita.formic.forms.actions.handlers;
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

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionContext;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.controllers.widget.WidgetController;
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.forms.validation.ValidatorException;

/**
 * Predefined save action to persist changes in form's main entity.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class SaveActionHandler extends AbstractActionHandler {

    public SaveActionHandler(MainController mc, Router router) {
        super(mc, router);
    }

    @Override
    public void handle(ActionContext actionContext, UserAction action) {
        boolean valid = true;
        if (StringUtils.isNotBlank(action.getController())) {
            ViewWidget rootWidget = actionContext.getViewController().getRootWidget();
            // check all controllers exits before method is executed
            List<WidgetController> ctrlList = ActionHandlerHelper.getControllers(rootWidget, action);
            for (WidgetController widgetController : ctrlList) {
                valid &= widgetController.save();
            }
        } else {
            // Use main form controller
            ViewController viewController = actionContext.getViewController();
            WidgetController widgetController = viewController.getMainWidgetController();
            valid = widgetController.save();
        }
        if (!valid) {
            throw new ValidatorException("A error occurred during form validation.");
        }
    }

    @Override
    public String getSuccessMessage(ActionContext actionContext, UserAction action) {
        return Config.getInstance().getStringResource(R.string.action_save_success);
    }

    @Override
    protected String getErrorMessage(UserAction action, Exception e) {
        if (e instanceof ValidatorException) {
            return Config.getInstance().getStringResource(R.string.action_generic_invalid_form);
        } else {
            return super.getErrorMessage(action, e);
        }
    }

}
