package es.jcyl.ita.frmdrd.ui.form;

import java.io.Serializable;

public class FormEntity implements Serializable {
    protected String id;
    protected String name;

    protected String renderCondition;
    protected String rerender;

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

    public String getRerender() {
        return rerender;
    }

    public void setRerender(String rerender) {
        this.rerender = rerender;
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