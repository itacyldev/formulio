package es.jcyl.ita.frmdrd.lifecycle.phase;

import es.jcyl.ita.frmdrd.context.Context;
import es.jcyl.ita.frmdrd.ui.form.UIField;

public class ProcessValidationsPhase extends Phase {

    public ProcessValidationsPhase() {
        this.id = PhaseId.PROCESS_VALIDATIONS;
    }

    @Override
    public void execute(Context updateContext) {
        if (updateContext != null) {
            Context formContext = lifecycle.getMainContext().getContext("form");

            for (String idField : updateContext.keySet()) {
                UIField fieldConfig = (UIField) formContext.get(idField);
                String newValue = (String) updateContext.get(idField);
                validateField(fieldConfig, newValue);
            }
        }


    }

    private boolean validateField(UIField fieldConfig, String newValue) {

        return true;
    }
}
