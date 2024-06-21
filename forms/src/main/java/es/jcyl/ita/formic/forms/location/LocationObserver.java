package es.jcyl.ita.formic.forms.location;


import java.util.Observable;
import java.util.Observer;

public class LocationObserver implements Observer {

    private boolean locationUpdated = false;

    @Override
    public void update(Observable o, Object response) {
        locationUpdated = true;
        //TODO
    }

    public void waitUpdateLocation(long initTime) {
        while (!locationUpdated) {
//            if (System.currentTimeMillis() - initTime > LocationService.MAX_ELAPSED_TIME) {
//                //TODO throw timeout exception
//                break;
//            }
        }


    }
}
