package es.jcyl.ita.frmdrd.configuration;

import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class DataBindings {

    private static Map<String, View> bindings = new HashMap<>();

    /**
     *
     * @param id
     * @param input
     */
    public static void registerView(String id, View input) {

        bindings.put(id, input);
    }

    /**
     *
     * @param id
     * @return
     */
    public static View getView(String id) {
        return bindings.get(id);
    }


}
