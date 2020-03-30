package es.jcyl.ita.frmdrd.interceptors;

import es.jcyl.ita.frmdrd.MainController;
import es.jcyl.ita.frmdrd.lifecycle.Lifecycle;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;

public class OnChangeFieldInterceptor {

    public void onChange(UIComponent component) {
        MainController.getInstance().doUserAction(component);
    }

}
