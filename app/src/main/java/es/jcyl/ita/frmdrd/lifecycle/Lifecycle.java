package es.jcyl.ita.frmdrd.lifecycle;


import es.jcyl.ita.frmdrd.context.CompositeContext;
import es.jcyl.ita.frmdrd.context.Context;
import es.jcyl.ita.frmdrd.context.OrderedCompositeContext;
import es.jcyl.ita.frmdrd.lifecycle.phase.BuildFormContextPhase;
import es.jcyl.ita.frmdrd.lifecycle.phase.BuildParamContextPhase;
import es.jcyl.ita.frmdrd.lifecycle.phase.LoadLocalContextsPhase;
import es.jcyl.ita.frmdrd.lifecycle.phase.Phase;
import es.jcyl.ita.frmdrd.lifecycle.phase.ProcessValidationsPhase;
import es.jcyl.ita.frmdrd.lifecycle.phase.RenderViewPhase;

public class Lifecycle {

    private Phase[] phases;

    private CompositeContext context;

    private String formId;


    public Lifecycle(String formId) {
        this.phases = new Phase[]{new BuildParamContextPhase(),
                new BuildFormContextPhase(), new ProcessValidationsPhase(),
                new LoadLocalContextsPhase(), new RenderViewPhase()};

        this.formId = formId;
    }

    public void doExecute(Context context) {
        this.context = new OrderedCompositeContext();
        this.context.addContext(context);

        execute(this.context, 0);
    }

    public void execute(Context context, Integer phaseId) {

        for (int i = phaseId; i < phases.length; i++) {
            phases[i].doPhase(context, this);
        }
    }

    public String getFormId() {
        return formId;
    }
}
