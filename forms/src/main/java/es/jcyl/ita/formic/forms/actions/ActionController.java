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

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.handlers.CreateEntityActionHandler;
import es.jcyl.ita.formic.forms.actions.handlers.DeleteActionHandler;
import es.jcyl.ita.formic.forms.actions.handlers.DeleteFromListActionHandler;
import es.jcyl.ita.formic.forms.actions.handlers.EmptyActionHandler;
import es.jcyl.ita.formic.forms.actions.handlers.JobActionHandler;
import es.jcyl.ita.formic.forms.actions.handlers.JsActionHandler;
import es.jcyl.ita.formic.forms.actions.handlers.SaveActionHandler;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.forms.validation.ValidatorException;
import es.jcyl.ita.formic.forms.view.UserMessagesHelper;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ActionController {

    private static final String REFRESH_THIS = "this";
    private static final String REFRESH_ALL = "all";
    private final Map<String, ActionHandler> handlerMap = new HashMap<>();
    private final MainController mc;
    private final Router router;
    private UserAction currentAction;

    private ActionResume resumableAction;

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
        register(ActionType.JOB, new JobActionHandler(mc, router));
    }

    public void register(ActionType type, ActionHandler handler) {
        register(type.name(), handler);
    }

    public void register(String type, ActionHandler handler) {
        handlerMap.put(type.toLowerCase(), handler);
    }

    public synchronized void execAction(UserAction action) {
        if (action.getOrigin() != null && action.getOrigin() != mc.getViewController()) {
            // Make sure the formController referred by the action is the current form controller,
            // in other case dismiss action to prevent executing delayed actions
            return;
        }
        try {
            // create context for action execution
            ActionContext actionContext = new ActionContext(mc.getViewController(),
                    mc.getRenderingEnv().getAndroidContext());
            if (action instanceof UserCompositeAction) {
                doExecAction(actionContext, (UserCompositeAction) action, 0);
            } else {
                doExecAction(actionContext, action);
            }
        } catch (Throwable e) {
            String msg = App.getInstance().getStringResource(R.string.action_generic_error);
            error(msg, e);
            mc.renderBack();
            // show error message
            UserMessagesHelper.toast(mc.getRenderingEnv().getAndroidContext(), msg);
        }
    }

    /**
     * Execute composite actions, run action sequence applying navigation for the last action. this.currentAction will
     * point to the last action and the end of the CompositeAction.
     *
     * @param actionContext
     * @param action
     */
    private void doExecAction(ActionContext actionContext, UserCompositeAction action, int starting) {
        UserAction[] subActions = action.getActions();
        int i = 0;
        for (UserAction sbAction : subActions) {
            if (i < starting) {
                i++;
                continue;
            }
            // navigate just in last action
            boolean doNavigate = i == subActions.length - 1;
            try {
                doExecAction(actionContext, sbAction, doNavigate, true);
                if (sbAction.isStopFlowControl()) {
                    // the actions needs user interaction or runs in a separate process, set as
                    // resumable and end current execution
                    registerResumableAction(actionContext, action, i + 1);
                    break;
                }
            } catch (StopCompositeActionException e) {
                break;
            }
            i++;
        }
    }

    private void registerResumableAction(ActionContext actionContext, UserCompositeAction action, int i) {
        this.resumableAction = new ActionResume(actionContext, action, i);
    }

    /**
     * Resumes current action
     */
    public void resumeAction() {
        if (this.resumableAction == null) {
            return;
        }
        try {
            doExecAction(resumableAction.actionContext, resumableAction.action, resumableAction.pos);
        } catch (Throwable e) {
            String msg = App.getInstance().getStringResource(R.string.action_generic_error);
            error(msg, e);
            mc.renderBack();
            // show error message
            UserMessagesHelper.toast(mc.getRenderingEnv().getAndroidContext(), msg);
        }
        this.resumableAction = null;
    }

    private void doExecAction(ActionContext actionContext, UserAction action) {
        doExecAction(actionContext, action, true, false);
    }

    /**
     * Execute basic actions
     *
     * @param actionContext
     * @param action
     * @param withNavigation
     */
    private void doExecAction(ActionContext actionContext, UserAction action, boolean withNavigation, boolean rethrow) {
        ActionHandler handler = handlerMap.get(action.getType().toLowerCase());
        if (handler == null) {
            throw new ConfigurationException(error("No action handler found for action type: " + action.getType()));
        }
        if (action.getOrigin() != null && action.getOrigin() != mc.getViewController()) {
            // Make sure the formController referred by the action is the current form controller,
            // in other case dismiss action to prevent executing delayed actions
            return;
        }
        this.currentAction = action;

        if (DevConsole.isDebugEnabled()) {
            DevConsole.debug(String.format("Executing action %s with ActionHandler: %s.",
                    action, handler));
        }
        mc.saveViewState();
        try {
            handler.handle(actionContext, action);
            if (withNavigation) {
                String msg = handler.getSuccessMessage(actionContext, action);
                UserAction navAction = handler.prepareNavigation(actionContext, action);
                resolveNavigation(actionContext, navAction, msg);
            }
        } catch (UserActionException | ValidatorException e) {
            mc.renderBack();
            handler.onError(actionContext, action, e);
            if (rethrow) {
                throw new StopCompositeActionException(e);
            }
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
        Collection<WidgetContextHolder> contextHolders = widget.getRootWidget().getContextHolders();
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

    public void clear() {
        this.currentAction = null;
    }

    public class ActionResume {
        int pos;
        UserCompositeAction action;
        ActionContext actionContext;

        public ActionResume(ActionContext actionContext, UserCompositeAction action, int pos) {
            this.actionContext = actionContext;
            this.action = action;
            this.pos = pos;
        }
    }
}

