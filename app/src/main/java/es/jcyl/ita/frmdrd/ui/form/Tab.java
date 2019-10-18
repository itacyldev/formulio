package es.jcyl.ita.frmdrd.ui.form;

import java.util.LinkedHashMap;
import java.util.Map;

public class Tab extends UIComponent {
    private Map<String, UIField> fields = new LinkedHashMap<>();

    public Map<String, UIField> getFields(){
        return fields;
    }

    public UIField getField(String id) {
        return fields.get(id);
    }

    public void addField(final UIField UIField) {
        fields.put(UIField.getId(), UIField);
    }
}