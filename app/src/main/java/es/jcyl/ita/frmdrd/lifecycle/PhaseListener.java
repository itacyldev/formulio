package es.jcyl.ita.frmdrd.lifecycle;

import java.io.Serializable;
import java.util.EventListener;

public abstract interface PhaseListener extends EventListener, Serializable {
    public abstract void afterPhase(PhaseEvent paramPhaseEvent);

    public abstract void beforePhase(PhaseEvent paramPhaseEvent);

    public abstract Integer getPhaseId();
}
