package es.jcyl.ita.formic.forms.actions.interceptors;

import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.actions.UserAction;

public class ViewUserActionInterceptor {

    private final ActionController actionController;
    private boolean disabled = false;

    public ViewUserActionInterceptor(ActionController actionController){
        this.actionController = actionController;
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