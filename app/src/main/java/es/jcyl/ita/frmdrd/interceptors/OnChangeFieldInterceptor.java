package es.jcyl.ita.frmdrd.interceptors;

import es.jcyl.ita.frmdrd.MainController;
import es.jcyl.ita.frmdrd.lifecycle.Lifecycle;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;

public class OnChangeFieldInterceptor {


    private Lifecycle lifecycle;

    public OnChangeFieldInterceptor(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public void onChange(UIComponent component) {
//        Context eventContext = new BasicContext();
//        eventContext.put("fieldId", fieldId);
//        lifecycle.execute(Phase.PhaseId.PROCESS_VALIDATIONS.ordinal(), eventContext);

        MainController.getInstance().doUserAction(component);

    }

}
