package es.jcyl.ita.formic.forms.actions.events;

import java.io.Serializable;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.controllers.UIParam;
import es.jcyl.ita.formic.forms.el.JexlFormUtils;

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
        UserAction action = event.getHandler();
        if (action == null) {
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
    }

    private UserAction createUserAction(Event event) {
        UIComponent component = event.getSource();
        UIAction userAction = component.getAction();
        if (userAction == null) {
            // no defined action for current event
            return null;
        }
        Context context = (event.getContext() != null) ? event.getContext() : mc.getGlobalContext();
        UserAction action = new UserAction(component.getAction(), component);
        if (component.hasParams()) {
            for (UIParam param : component.getParams()) {
                Object value = JexlFormUtils.eval(context, param.getValue());
                action.addParam(param.getName(), (Serializable) value);
            }
        }
        return action;
    }
//
//    public void doAction(UserAction action) {
//        if (disabled || actionController == null) {
//            return;
//        }
//        actionController.doUserAction(action);
//    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
