package es.jcyl.ita.formic.forms.actions;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.handlers.CreateEntityActionHandler;
import es.jcyl.ita.formic.forms.actions.handlers.DeleteActionHandler;
import es.jcyl.ita.formic.forms.actions.handlers.DeleteFromListActionHandler;
import es.jcyl.ita.formic.forms.actions.handlers.EmptyActionHandler;
import es.jcyl.ita.formic.forms.actions.handlers.SaveActionHandler;
import es.jcyl.ita.formic.forms.components.form.WidgetContextHolder;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.forms.validation.ValidatorException;
import es.jcyl.ita.formic.forms.view.UserMessagesHelper;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.widget.Widget;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ActionController {

    private static final String REFRESH_THIS = "this";
    private static final String REFRESH_ALL = "all";
    private final Map<String, ActionHandler> actionMap = new HashMap<>();
    private final MainController mc;
    private final Router router;
    private UserAction currentAction;

    public ActionController(MainController mc, Router router) {
        this.mc = mc;
        this.router = router;
        // default actions
        register(ActionType.SAVE, new SaveActionHandler(mc, router));
        ActionHandler navHandler = new EmptyActionHandler(mc, router);
        register(ActionType.BACK, navHandler);
        register(ActionType.CANCEL, navHandler);
        register(ActionType.NAV, navHandler);
        register(ActionType.DELETE, new DeleteActionHandler(mc, router));
        register(ActionType.DELETE_LIST, new DeleteFromListActionHandler(mc, router));
        register(ActionType.CREATE, new CreateEntityActionHandler(mc, router));
        register(ActionType.JS, new JsActionHandler(mc, router));
    }

    public void register(ActionType type, ActionHandler handler) {
        register(type.name(), handler);
    }

    public void register(String type, ActionHandler handler) {
        actionMap.put(type.toLowerCase(), handler);
    }

    public synchronized void doUserAction(UserAction action) {
        if (action.getOrigin() != null && action.getOrigin() != mc.getFormController()) {
            // Make sure the formController referred by the action is the current form controller,
            // in other case dismiss action to prevent executing delayed actions
            return;
        }
        this.currentAction = action;

        ActionHandler handler;
        try {
            // create context for action execution
            ActionContext actionContext = new ActionContext(mc.getFormController(),
                    mc.getRenderingEnv().getAndroidContext());
            handler = actionMap.get(action.getType().toLowerCase());
            if (handler == null) {
                throw new ConfigurationException(error("No action handler found for action type: " + action.getType()));
            }
            if (DevConsole.isDebugEnabled()) {
                DevConsole.debug(String.format("Executing action %s with ActionHandler: %s.",
                        action, handler));
            }
            mc.saveViewState();
            try {
                handler.handle(actionContext, action);
                String msg = handler.getSuccessMessage(actionContext, action);
                resolveNavigation(actionContext, action, msg);
            } catch (UserActionException | ValidatorException e) {
                mc.renderBack();
                mc.restoreViewState();
                handler.onError(actionContext, action, e);
            }
        } catch (Exception e) {
            mc.renderBack();
            mc.restoreViewState();
            // show error message
            String msg = Config.getInstance().getStringResource(R.string.action_generic_error);
            error(msg, e);
            UserMessagesHelper.toast(mc.getRenderingEnv().getAndroidContext(), msg);
        }
    }


    protected void resolveNavigation(ActionContext actionContext, UserAction action, String msg) {
        if (action.isRefreshSet()) {
            // update current view after action completion
            String refresh = action.getRefresh();
            if (REFRESH_THIS.equals(refresh.toLowerCase())) {
                mc.updateView(action.getWidget().getWidgetContext().getWidget());
            } else if (REFRESH_ALL.equals(refresh.toLowerCase())) {
                mc.renderBack();
            } else {
                // render the widget identified by the the id of the 'refresh' attribute
                Widget widget = action.getWidget();
                Widget componentWidget = ViewHelper.findComponentWidget(widget.getRootView(), refresh);
                if (componentWidget == null) {
                    DevConsole.error(createMessage(action, widget));
                    throw new UserActionException("Invalid widget id reference: " + refresh);
                }
                mc.updateView(componentWidget);
            }
            if (StringUtils.isNotBlank(msg)) {
                UserMessagesHelper.toast(actionContext.getViewContext(), msg);
            }
        } else if (StringUtils.isBlank(action.getRoute())) {
            // no navigation, stay in form
            if (StringUtils.isNotBlank(msg)) {
                UserMessagesHelper.toast(actionContext.getViewContext(), msg);
            }
        } else {
            router.navigate(actionContext, action, msg);
        }
    }

    private String createMessage(UserAction action, Widget widget) {
        List<WidgetContextHolder> contextHolders = widget.getRootWidget().getContextHolders();
        String holderIds;
        if (contextHolders == null) {
            holderIds = "none";
        } else {
            StringBuffer stb = new StringBuffer();
            for (WidgetContextHolder ctx : contextHolders) {
                stb.append(ctx.getHolderId() + ", ");
            }
            holderIds = stb.substring(0, stb.length() - 2);
        }
        String msg = String.format("Error while trying to execute action, the refresh attribute in component " +
                        "[%s] is invalid: [%s] valid values are: 'this', 'all' and the id of a widget that " +
                        "implements WidgetStateHolder, typically a form or dataListItem. \n" +
                        " In this view these ones are defined: (%s)", widget.getComponentId(),
                action.getRefresh(), holderIds);

        return msg;
    }

    public MainController getMc() {
        return mc;
    }

    public UserAction getCurrentAction() {
        return this.currentAction;
    }
}

