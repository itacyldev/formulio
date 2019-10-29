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

    private CompositeContext mainContext;

    private String formId;


    public Lifecycle(String formId) {
        this.phases = new Phase[]{new BuildParamContextPhase(),
                new BuildFormContextPhase(), new ProcessValidationsPhase(),
                new LoadLocalContextsPhase(), new RenderViewPhase()};

        this.formId = formId;
    }

    public void doExecute(Context context) {
        this.mainContext = new OrderedCompositeContext();
        this.mainContext.addContext(context);

        execute(0, null);
    }

    public void execute(Integer phaseId, Context context) {
        for (int i = phaseId; i < phases.length; i++) {
            phases[i].doPhase(this, context);
        }
    }

    public String getFormId() {
        return formId;
    }

    public void addContext(Context context) {
        mainContext.addContext(context);

    }

    public CompositeContext getMainContext() {
        return mainContext;
    }
}
