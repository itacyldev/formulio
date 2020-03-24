package es.jcyl.ita.frmdrd.ui.components;

import java.util.LinkedHashMap;
import java.util.Map;

public class UITab extends UIComponent {
    private Map<String, UIComponent> fields = new LinkedHashMap<>();

    public Map<String, UIComponent> getFields() {
        return fields;
    }

    public UIComponent getField(String id) {
        return fields.get(id);
    }

    public void addComponent(final UIComponent component) {
        component.setParent(this);
        this.addChild(component);
        fields.put(component.getId(), component);
    }

}