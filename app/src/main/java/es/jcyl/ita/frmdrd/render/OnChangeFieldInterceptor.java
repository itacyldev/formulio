package es.jcyl.ita.frmdrd.render;

import org.apache.commons.lang.StringUtils;

import es.jcyl.ita.frmdrd.context.Context;
import es.jcyl.ita.frmdrd.lifecycle.Lifecycle;
import es.jcyl.ita.frmdrd.lifecycle.phase.Phase;
import es.jcyl.ita.frmdrd.ui.form.UIField;

public class OnChangeFieldInterceptor {


    private Lifecycle lifecycle;

    public OnChangeFieldInterceptor(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public void onChange(UIField UIField, Object value) {
        lifecycle.execute(Phase.PhaseId.PROCESS_VALIDATIONS.ordinal());

        if (validate(UIField, value)) {
            updateContext(UIField, value);

            String update = UIField.getUpdate();
            if (StringUtils.isNotEmpty(update)) {
                this.update(update);
            }
        }
    }

    private boolean validate(UIField field, Object value) {

        return true;
    }

    private void updateContext(UIField field, Object value) {

    }

    private void update(String update) {
        Context context = null;
        lifecycle.execute(Phase.PhaseId.RENDER_VIEW.ordinal());
    }

}
