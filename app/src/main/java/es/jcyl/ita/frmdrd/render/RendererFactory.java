package es.jcyl.ita.frmdrd.render;

import es.jcyl.ita.frmdrd.lifecycle.Lifecycle;
import es.jcyl.ita.frmdrd.ui.form.UIField;

public class RendererFactory {

    public UIFieldRenderer getComponentRenderer(UIField UIField,
                                                Lifecycle lifecycle) {
        UIFieldRenderer renderer = null;

        String type = UIField.getType();

        switch (type) {
            case "TEXT":
                renderer = new TextFieldRenderer(lifecycle);
                break;
            case "BOOLEAN":
                renderer = new CheckBoxFieldRenderer(lifecycle);
                break;
            case "DATE":
                renderer = new DateFieldRenderer(lifecycle);
                break;
            default:
                break;
        }


        return renderer;
    }
}
