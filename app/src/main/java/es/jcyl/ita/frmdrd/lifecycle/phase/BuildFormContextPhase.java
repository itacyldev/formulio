package es.jcyl.ita.frmdrd.lifecycle.phase;


import es.jcyl.ita.frmdrd.context.Context;
import es.jcyl.ita.frmdrd.context.impl.BasicContext;

public class BuildFormContextPhase extends Phase {

    public BuildFormContextPhase() {
        this.id = PhaseId.BUILD_FORM_CONTEXT;
    }

    @Override
    public void execute(Context context) {
        String formId = (String) context.get("lifecycle.formId");


        Context formContext = new BasicContext("form");


    }

}
