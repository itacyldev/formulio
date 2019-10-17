package es.jcyl.ita.frmdrd.lifecycle;


import es.jcyl.ita.frmdrd.context.Context;
import es.jcyl.ita.frmdrd.lifecycle.phase.BuildFormContextPhase;
import es.jcyl.ita.frmdrd.lifecycle.phase.BuildParamContextPhase;
import es.jcyl.ita.frmdrd.lifecycle.phase.LoadLocalContextsPhase;
import es.jcyl.ita.frmdrd.lifecycle.phase.Phase;
import es.jcyl.ita.frmdrd.lifecycle.phase.ProcessValidationsPhase;
import es.jcyl.ita.frmdrd.lifecycle.phase.RenderViewPhase;

public class Lifecycle {

    private Phase[] phases;

    public Lifecycle() {
        this.phases = new Phase[]{new BuildParamContextPhase(),
                new BuildFormContextPhase(), new ProcessValidationsPhase(),
                new LoadLocalContextsPhase(), new RenderViewPhase()};
    }

    void execute(Context context, Integer phaseId) {

        for (int i = phaseId; i < phases.length; i++) {
            phases[i].execute(context);
        }
    }
}
