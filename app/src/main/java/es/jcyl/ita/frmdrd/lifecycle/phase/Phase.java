package es.jcyl.ita.frmdrd.lifecycle.phase;


import es.jcyl.ita.crtrepo.context.Context;
import es.jcyl.ita.frmdrd.lifecycle.Lifecycle;

public abstract class Phase {

    protected Lifecycle lifecycle;

    public enum PhaseId {
        BUILD_PARAM_CONTEXT, BUILD_FORM_CONTEXT,
        PROCESS_VALIDATIONS, LOAD_LOCAL_CONTEXT, RENDER_VIEW, UPDATE_CONTEXT
    }

    protected PhaseId id;

    public void doPhase(Lifecycle lifecycle, Context phaseContext) {
        this.lifecycle = lifecycle;
        execute(phaseContext);
    }

    public abstract void execute(Context context);

    public PhaseId getId() {
        return id;
    }

}
