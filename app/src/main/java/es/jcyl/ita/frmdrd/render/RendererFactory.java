package es.jcyl.ita.frmdrd.render;

import es.jcyl.ita.frmdrd.ui.form.UIField;

public class RendererFactory {

    public UIComponentRenderer getComponentRenderer(UIField UIField) {
        UIComponentRenderer renderer = null;

        String type = UIField.getType();

        switch (type) {
            case "TEXT":
                renderer = new TextFieldRenderer();
                break;
            case "BOOLEAN":
                renderer = new CheckBoxFieldRenderer();
                break;
            case "DATE":
                renderer = new DateFieldRenderer();
                break;
            default:
                break;
        }


        return renderer;
    }
}
