package es.jcyl.ita.frmdrd.render;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Map;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.ui.form.UIField;
import es.jcyl.ita.frmdrd.ui.form.UIForm;
import es.jcyl.ita.frmdrd.ui.form.Tab;
import es.jcyl.ita.frmdrd.ui.form.UIComponent;

public class FormRenderer {

    public View render(Context context, UIForm UIForm) {
        View view = null;

        String formType = "LinearLayOut";

        switch (formType) {
            case "LinearLayOut":
                view = renderLinearLayout(context, UIForm);
        }

        return view;
    }

    private View renderLinearLayout(Context context, UIForm UIForm) {
        LinearLayout layout = ((Activity) context).findViewById(R.id.fields_linear_layout);

        Map<String, Tab> tabs = UIForm.getTabs();

        for (Tab tab : tabs.values()) {
            renderFields(context, tab.getFields(), layout);
        }


        return layout;
    }

    private void renderFields(Context context,
                              Map<String, UIField> components,
                              ViewGroup parent) {
        RendererFactory rendererFact = new RendererFactory();

        for (UIComponent component : components.values()) {
            UIComponentRenderer componentRenderer =
                    rendererFact.getComponentRenderer((UIField) component);

            componentRenderer.render(context, component, parent);
        }

    }

}
