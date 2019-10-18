package es.jcyl.ita.frmdrd.lifecycle.phase;

import androidx.lifecycle.Lifecycle;
import es.jcyl.ita.frmdrd.context.Context;

public abstract class Phase {

    public enum PhaseId {
        BUILD_PARAM_CONTEXT, BUILD_FORM_CONTEXT,
        PROCESS_VALIDATIONS, LOAD_LOCAL_CONTEXT, RENDER_VIEW, UPDATE_CONTEXT
    }

    protected PhaseId id;

    public void doPhase(Context context, Lifecycle lifecycle) {
        execute(context);
    }

    public abstract void execute(Context context);

    public PhaseId getId() {
        return id;
    }

}
