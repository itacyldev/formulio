package es.jcyl.ita.frmdrd.configuration;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.frmdrd.forms.FormController;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;

public class FormConfigHandler {

    private static Map<String, FormController> forms = new HashMap<String, FormController>();

    public static Map<String, FormController> getForms() {
        return forms;
    }

    public static FormController getForm(String formId) {
        return forms.get(formId);
    }

    public static void addForm(FormController form) {
        forms.put(form.getId(), form);
    }

}
