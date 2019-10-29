package es.jcyl.ita.frmdrd.render;

import es.jcyl.ita.frmdrd.context.Context;
import es.jcyl.ita.frmdrd.context.impl.BasicContext;
import es.jcyl.ita.frmdrd.lifecycle.Lifecycle;
import es.jcyl.ita.frmdrd.lifecycle.phase.Phase;
import es.jcyl.ita.frmdrd.ui.form.UIField;

public class OnChangeFieldInterceptor {


    private Lifecycle lifecycle;

    public OnChangeFieldInterceptor(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public void onChange(UIField field, Object value) {
        Context updateContext = new BasicContext("update");
        updateContext.put(field.getId(), value);

        lifecycle.execute(Phase.PhaseId.PROCESS_VALIDATIONS.ordinal(), updateContext);
    }

    private boolean validate(UIField field, Object value) {

        return true;
    }

    private void updateContext(UIField field, Object value) {

    }
}
