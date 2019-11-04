package es.jcyl.ita.frmdrd.render;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;
import java.util.Map;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.configuration.DataBindings;
import es.jcyl.ita.frmdrd.lifecycle.Lifecycle;
import es.jcyl.ita.frmdrd.ui.form.UIField;
import es.jcyl.ita.frmdrd.ui.form.UIForm;
import es.jcyl.ita.frmdrd.ui.form.UITab;

public class FormRenderer {

    private Lifecycle lifecycle;

    public FormRenderer(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public View render(Context context, UIForm form) {
        View view = null;

        String formType = "LinearLayOut";

        switch (formType) {
            case "LinearLayOut":
                view = renderLinearLayout(context, form);
        }

        view.setVisibility(View.VISIBLE);
        view.setTag("form");
        DataBindings.registerView(form.getId(), view);

        return view;
    }

    public void render(List<UIField> fields) {
        RendererFactory rendererFact = new RendererFactory();
        for (UIField field : fields) {
            UIFieldRenderer fieldRenderer =
                    rendererFact.getComponentRenderer(field, lifecycle);
            fieldRenderer.update(field);
        }
    }

    private View renderLinearLayout(Context context, UIForm form) {
        LinearLayout layout = ((Activity) context).findViewById(R.id.fields_linear_layout);

        Map<String, UITab> tabs = form.getTabs();

        for (UITab tab : tabs.values()) {
            renderFields(context, tab.getFields(), layout);
        }


        return layout;
    }

    private void renderFields(Context context,
                              Map<String, UIField> fields,
                              ViewGroup parent) {
        RendererFactory rendererFact = new RendererFactory();

        for (UIField field : fields.values()) {
            UIFieldRenderer fieldRenderer =
                    rendererFact.getComponentRenderer(field, lifecycle);

            View view = fieldRenderer.render(context, field);
            parent.addView(view);
        }

    }

}
