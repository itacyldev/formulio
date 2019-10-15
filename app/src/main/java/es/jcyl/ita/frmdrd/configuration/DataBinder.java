package es.jcyl.ita.frmdrd.configuration;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.frmdrd.ui.form.FormEntity;

public class DataBinder {

    private static Map<Integer, FormEntity> bindings = new HashMap<>();


    public static void registerBinding(Integer id, FormEntity formEntity) {
        bindings.put(id, formEntity);
    }

    public static FormEntity getBinding(Integer id) {
        return bindings.get(id);
    }




}
