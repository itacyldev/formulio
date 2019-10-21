package es.jcyl.ita.frmdrd.lifecycle.phase;


import es.jcyl.ita.frmdrd.context.CompositeContext;
import es.jcyl.ita.frmdrd.context.Context;
import es.jcyl.ita.frmdrd.context.impl.BasicContext;

public class BuildFormContextPhase extends Phase {

    public BuildFormContextPhase() {
        this.id = PhaseId.BUILD_FORM_CONTEXT;
    }

    @Override
    public void execute(Context context) {
        Context formContext = new BasicContext("form");

        //TODO create form context

        ((CompositeContext)context).addContext(formContext);
    }

}
