package es.jcyl.ita.frmdrd.lifecycle.phase;

import es.jcyl.ita.frmdrd.context.Context;
import es.jcyl.ita.frmdrd.context.impl.FormContext;
import es.jcyl.ita.frmdrd.ui.form.UIField;
import es.jcyl.ita.frmdrd.ui.form.UIForm;

public class ProcessValidationsPhase extends Phase {

    public ProcessValidationsPhase() {
        this.id = PhaseId.PROCESS_VALIDATIONS;
    }

    @Override
    public void execute(Context updateContext) {
        FormContext formContext = (FormContext) lifecycle.getMainContext().getContext(
                "form");
        if (updateContext != null) {
            for (String idField : updateContext.keySet()) {
                UIField fieldConfig = (UIField) formContext.get(idField);
                fieldConfig.processValidators(lifecycle.getMainContext());
            }
        } else {
            UIForm form = formContext.getRoot();
            form.processValidators(lifecycle.getMainContext());
        }
    }
}
