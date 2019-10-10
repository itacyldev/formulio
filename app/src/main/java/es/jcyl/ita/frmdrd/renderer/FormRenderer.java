package es.jcyl.ita.frmdrd.renderer;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Map;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.ui.form.Field;
import es.jcyl.ita.frmdrd.ui.form.Form;
import es.jcyl.ita.frmdrd.ui.form.Tab;

public class FormRenderer {

    public View render(Context context, Form form) {
        View view = null;

        String formType = "LinearLayOut";

        switch (formType) {
            case "LinearLayOut":
                view = renderLinearLayout(context, form);
        }

        return view;
    }

    private View renderLinearLayout(Context context, Form form) {
        LinearLayout layout = ((Activity) context).findViewById(R.id.fields_linear_layout);

        Map<String, Tab> tabs = form.getTabs();

        for(Tab tab: tabs.values()){
            renderFields(context,tab.getFields(), layout);
        }


        return layout;
    }

    private void renderFields(Context context, Map<String, Field> fields, ViewGroup parent) {
        RendererFactory rendererFact = new RendererFactory();

        for (Field field : fields.values()) {
            IFieldRenderer fieldRenderer = rendererFact.getFieldRenderer(field);

            fieldRenderer.render(context, field, parent);
        }

    }

}
