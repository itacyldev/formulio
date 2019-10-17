package es.jcyl.ita.frmdrd.ui.form;

import java.io.Serializable;
import java.util.List;

public class UIComponent implements Serializable {
    protected String id;
    protected String name;

    protected UIComponent parent;
    protected List<UIComponent> children;

    protected String renderCondition;
    protected String update;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
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
        if (id != null && name != null) {
            output += " / ";
        }
        if (name != null) {
            output += name;
        }

        if (output.length() > 0) {
            return output;
        } else {
            return super.toString();
        }
    }
}