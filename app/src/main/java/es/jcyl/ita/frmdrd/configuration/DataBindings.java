package es.jcyl.ita.frmdrd.configuration;

import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class DataBindings {

    private static Map<String, View> bindings = new HashMap<>();


    public static void registerView(String id, View input) {
        bindings.put(id, input);
    }

    public static View getView(String id) {
        return bindings.get(id);
    }


}
