package es.jcyl.ita.frmdrd.ui.form;

import java.util.LinkedHashMap;
import java.util.Map;

public class Tab extends FormEntity {
    private Map<String, Field> fields = new LinkedHashMap<>();

    public Map<String, Field> getFields(){
        return fields;
    }

    public Field getField(String id) {
        return fields.get(id);
    }

    public void addField(final Field field) {
        fields.put(field.getId(), field);
    }
}