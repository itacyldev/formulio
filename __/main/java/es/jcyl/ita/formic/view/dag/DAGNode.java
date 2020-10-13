package es.jcyl.ita.formic.forms.view.dag;

import es.jcyl.ita.formic.forms.components.UIComponent;

public class DAGNode {

    public enum TYPE {SOURCE, FIELD}

    private String id;
    private TYPE type;

    public UIComponent getComponent() {
        return component;
    }

    public DAGNode() {
    }

    public DAGNode(String id, UIComponent component, TYPE type) {
        this.id = id;
        this.component = component;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public void setComponent(UIComponent component) {
        this.component = component;
    }

    private UIComponent component;

}
