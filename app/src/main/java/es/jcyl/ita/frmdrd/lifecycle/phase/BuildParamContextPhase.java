package es.jcyl.ita.frmdrd.lifecycle.phase;

import es.jcyl.ita.frmdrd.context.Context;
import es.jcyl.ita.frmdrd.context.impl.BasicContext;

public class BuildParamContextPhase extends Phase {

    public BuildParamContextPhase(){
        this.id = PhaseId.BUILD_PARAM_CONTEXT;
    }

    @Override
    public void execute(Context context) {
        Context paramContext = new BasicContext("param");
    }
}