package es.jcyl.ita.formic.forms.actions.events;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.UserActionHelper;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.controllers.UIActionGroup;
import es.jcyl.ita.formic.forms.controllers.UIParam;
import es.jcyl.ita.formic.forms.el.JexlFormUtils;
import es.jcyl.ita.formic.forms.view.UserMessagesHelper;

/**
 * Receives an event to fire on a component, retrieves proper handler and creates the UserAction
 * object.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UserEventInterceptor {

    private final MainController mc;
    private final ActionController actionController;
    private boolean disabled = false;
    // handlers
    private final OnInputChangeEventHandler changeHandler;
    private final OnClickEventHandler clickHandler;

    public UserEventInterceptor(ActionController actionController) {
        this.mc = actionController.getMc();
        this.actionController = actionController;
        // handlers. Not sure will need here a map
        this.clickHandler = new OnClickEventHandler(this.mc, actionController);
        this.changeHandler = new OnInputChangeEventHandler(this.mc, actionController);
    }

    /**
     * Receives event notification from the view and creates an UserAction from the component
     * information to execution it using a click/change handler
     *
     * @param event
     */
    public void notify(Event event) {
        if (disabled) {
            return;
        }
        try {
            UserAction action = event.getHandler();
            if (action == null) {
                // create action from component declaration
                action = createUserAction(event);
                event.setHandler(action);
            }
            switch (event.getType()) {
                case CLICK:
                    clickHandler.handle(event);
                    break;
                case CHANGE:
                    changeHandler.handle(event);
                    break;
            }
        } catch (Throwable e) {
            String msg = App.getInstance().getStringResource(R.string.action_generic_error);
            error(msg, e);
            mc.renderBack();
            // show error message
            UserMessagesHelper.toast(mc.getRenderingEnv().getAndroidContext(), msg);
        }

    }

    private UserAction createUserAction(Event event) {
        UIComponent component = event.getSource().getComponent();
        UIAction uiAction = component.getAction();
        if (uiAction == null) {
            // no defined action for current event
            return null;
        }
        Context context = (event.getContext() != null) ? event.getContext() : event.getSource().getWidgetContext();
        UserAction action = UserActionHelper.newAction(component.getAction(), component);
        action.setWidget(event.getSource());
        UserActionHelper.evalActionParams(context, uiAction, action);
        return action;
    }


    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
