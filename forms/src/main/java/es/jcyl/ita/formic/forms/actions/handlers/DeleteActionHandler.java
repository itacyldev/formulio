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

import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionContext;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.controllers.widget.WidgetController;
import es.jcyl.ita.formic.forms.router.Router;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class DeleteActionHandler extends AbstractActionHandler {

    public DeleteActionHandler(MainController mc, Router router) {
        super(mc, router);
    }

    @Override
    public void handle(ActionContext actionContext, UserAction action) {
        if (StringUtils.isNotBlank(action.getController())) {
            ViewWidget rootWidget = action.getWidget().getRootWidget();
            // check all controllers exits before method is executed
            List<WidgetController> ctrlList = ActionHandlerHelper.getControllers(rootWidget, action);
            for (WidgetController controller : ctrlList) {
                controller.delete();
            }
        } else {
            // Use main form controller
            ViewController viewController = actionContext.getViewController();
            WidgetController widgetController = viewController.getMainWidgetController();
            widgetController.delete();
        }
    }
    
    @Override
    public String getSuccessMessage(ActionContext actionContext, UserAction action) {
        return App.getInstance().getStringResource(R.string.action_delete_success);
    }

    @Override
    protected String getErrorMessage(UserAction action, Exception e) {
        return App.getInstance().getStringResource(R.string.action_delete_error);
    }

}
