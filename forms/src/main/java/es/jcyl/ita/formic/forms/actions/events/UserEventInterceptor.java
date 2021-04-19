package es.jcyl.ita.formic.forms.actions.events;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.link.UIParam;
import es.jcyl.ita.formic.forms.el.JexlFormUtils;

public class UserEventInterceptor {

    private final ActionController actionController;
    private final MainController mc;
    private boolean disabled = false;
    // handlers
    private final OnInputChangeEventHandler changeHandler;
    private final OnClickEventHandler clickHandler;

    public UserEventInterceptor(MainController mc, ActionController actionController) {
        this.mc = mc;
        this.actionController = actionController;

        // handlers. Not sure will need here a map
        this.clickHandler = new OnClickEventHandler(mc, actionController);
        this.changeHandler = new OnInputChangeEventHandler(mc, actionController);
    }

    public void notify(Event event) {
        UserAction action = event.getHandler();
        if(action == null){
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
        String handler = null;
        switch (event.getType()) {
            case CLICK:
                handler = component.getOnClickAction();
                break;
            case CHANGE:
                handler = component.getOnChangeAction();
                break;
        }
        if(StringUtils.isBlank(handler)){
            // no defined action for current event
            return null;
        }
        UserAction action = new UserAction(handler,component);
        action.setRoute(component.getr);
        component.get
        UserAction action = UserAction.navigate(component.getRoute(), component);
        if (component.hasParams()) {
            for (UIParam param : component.getParams()) {
                Object value = JexlFormUtils.eval(env.getContext(), param.getValue());
                action.addParam(param.getName(), (Serializable) value);
            }
        }
        return action
    }

    public void doAction(UserAction action) {
        if (disabled || actionController == null) {
            return;
        }
        actionController.doUserAction(action);
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
