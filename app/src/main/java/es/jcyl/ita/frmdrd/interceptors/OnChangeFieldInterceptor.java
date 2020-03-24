package es.jcyl.ita.frmdrd.interceptors;

import es.jcyl.ita.frmdrd.context.Context;
import es.jcyl.ita.frmdrd.context.impl.BasicContext;
import es.jcyl.ita.frmdrd.lifecycle.Lifecycle;
import es.jcyl.ita.frmdrd.lifecycle.phase.Phase;

public class OnChangeFieldInterceptor {


    private Lifecycle lifecycle;

    public OnChangeFieldInterceptor(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public void onChange(String fieldId) {
        Context eventContext = new BasicContext();
        eventContext.put("fieldId", fieldId);
        lifecycle.execute(Phase.PhaseId.PROCESS_VALIDATIONS.ordinal(), eventContext);
    }

}
