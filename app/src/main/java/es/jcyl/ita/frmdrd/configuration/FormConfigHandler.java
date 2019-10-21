package es.jcyl.ita.frmdrd.configuration;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.frmdrd.ui.form.UIForm;

public class FormConfigHandler {

    private static Map<String, UIForm> forms = new HashMap<String, UIForm>();

    public static Map<String, UIForm> getForms() {
        return forms;
    }

    public static UIForm getForm(String formId) {
        return forms.get(formId);
    }

    public static void addForm(UIForm form) {
        forms.put(form.getId(), form);
    }

}
