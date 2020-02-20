package es.jcyl.ita.frmdrd.render;

import android.content.Context;

import es.jcyl.ita.frmdrd.lifecycle.Lifecycle;
import es.jcyl.ita.frmdrd.ui.form.UIField;

public class RendererFactory {

    public UIFieldRenderer getComponentRenderer(Context context,
                                                UIField UIField,
                                                Lifecycle lifecycle) {
        UIFieldRenderer renderer = null;

        String type = UIField.getType();

        switch (type) {
            case "TEXT":
                renderer = new TextFieldRenderer(context, lifecycle);
                break;
            case "BOOLEAN":
                renderer = new CheckBoxFieldRenderer(context, lifecycle);
                break;
            case "DATE":
                renderer = new DateFieldRenderer(context, lifecycle);
                break;
            case "TABLE":
                renderer = new TableFieldRenderer(context, lifecycle);
            default:
                break;
        }


        return renderer;
    }
}
