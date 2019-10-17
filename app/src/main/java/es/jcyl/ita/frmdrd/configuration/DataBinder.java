package es.jcyl.ita.frmdrd.configuration;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.frmdrd.ui.form.UIComponent;

public class DataBinder {

    private static Map<Integer, UIComponent> bindings = new HashMap<>();


    public static void registerBinding(Integer id, UIComponent UIComponent) {
        bindings.put(id, UIComponent);
    }

    public static UIComponent getBinding(Integer id) {
        return bindings.get(id);
    }




}
