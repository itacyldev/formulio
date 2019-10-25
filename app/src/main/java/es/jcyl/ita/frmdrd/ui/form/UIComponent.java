package es.jcyl.ita.frmdrd.ui.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UIComponent implements Serializable {
    protected String id;
    protected UIComponent parent;
    protected List<UIComponent> children;

    protected String label;

    protected String renderCondition;
    protected String update;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public UIComponent getParent() {
        return parent;
    }

    public void setParent(UIComponent parent) {
        this.parent = parent;
    }

    public List<UIComponent> getChildren() {
        return children;
    }

    public void addChild(UIComponent child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getRenderCondition() {
        return renderCondition;
    }

    public void setRenderCondition(String renderCondition) {
        this.renderCondition = renderCondition;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }


    @Override
    public String toString() {
        String output = "";

        if (id != null) {
            output += id;
        }
        if (id != null && label != null) {
            output += " / ";
        }
        if (label != null) {
            output += label;
        }

        if (output.length() > 0) {
            return output;
        } else {
            return super.toString();
        }
    }
}