package es.jcyl.ita.frmdrd.ui.form;

import java.util.LinkedHashMap;
import java.util.Map;

public class UITab extends UIComponent {
    private Map<String, UIField> fields = new LinkedHashMap<>();

    public Map<String, UIField> getFields() {
        return fields;
    }

    public UIField getField(String id) {
        return fields.get(id);
    }

    public void addField(final UIField field) {
        field.setParent(this);
        this.addChild(field);
        fields.put(field.getId(), field);
    }
}