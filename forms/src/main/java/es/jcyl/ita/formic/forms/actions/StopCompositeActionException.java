package es.jcyl.ita.formic.forms.actions;

public class StopCompositeActionException extends UserActionException{
    public StopCompositeActionException(String msg) {
        super(msg);
    }

    public StopCompositeActionException(RuntimeException e) {
        super(e);
    }
}
