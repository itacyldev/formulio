package es.jcyl.ita.frmdrd.render;

import es.jcyl.ita.frmdrd.ui.form.Field;

public class RendererFactory {

    public UIComponentRenderer getComponentRenderer(Field field) {
        UIComponentRenderer renderer = null;

        String type = field.getType();

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
