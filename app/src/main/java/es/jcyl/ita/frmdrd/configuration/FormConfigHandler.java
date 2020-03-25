package es.jcyl.ita.frmdrd.configuration;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.frmdrd.ui.components.view.UIView;

public class FormConfigHandler {

    private static Map<String, UIView> forms = new HashMap<String, UIView>();

    public static Map<String, UIView> getForms() {
        return forms;
    }

    public static UIView getView(String formId) {
        return forms.get(formId);
    }

    public static void addForm(UIView form) {
        forms.put(form.getId(), form);
    }

}
