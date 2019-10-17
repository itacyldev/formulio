package es.jcyl.ita.frmdrd.lifecycle;

import androidx.lifecycle.Lifecycle;
import es.jcyl.ita.frmdrd.context.Context;

public class PhaseEvent {

    private Context context = null;
    private Integer phaseId = null;


    public PhaseEvent(Context context, Integer phaseId, Lifecycle lifecycle) {
        if ((phaseId == null) || (context == null) || (lifecycle == null)) {

            throw new NullPointerException();

        }

        this.phaseId = phaseId;
        this.context = context;

    }
    

    public Integer getPhaseId() {
        return this.phaseId;
    }

}

