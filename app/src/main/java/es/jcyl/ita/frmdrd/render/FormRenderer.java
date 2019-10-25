package es.jcyl.ita.frmdrd.render;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Map;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.lifecycle.Lifecycle;
import es.jcyl.ita.frmdrd.ui.form.UITab;
import es.jcyl.ita.frmdrd.ui.form.UIField;
import es.jcyl.ita.frmdrd.ui.form.UIForm;

public class FormRenderer {

    private Lifecycle lifecycle;

    public FormRenderer(Lifecycle lifecycle){
        this.lifecycle = lifecycle;
    }

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

        Map<String, UITab> tabs = UIForm.getTabs();

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

            fieldRenderer.render(context, field, parent);
        }

    }

}
