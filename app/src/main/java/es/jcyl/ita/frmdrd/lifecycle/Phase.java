package es.jcyl.ita.frmdrd.lifecycle;

import java.util.ListIterator;

import androidx.lifecycle.Lifecycle;
import es.jcyl.ita.frmdrd.context.Context;

public abstract class Phase {

    public void doPhase(Context context, Lifecycle lifecycle, ListIterator<PhaseListener> listeners) {

    }

    public abstract void execute(Context context);
}
