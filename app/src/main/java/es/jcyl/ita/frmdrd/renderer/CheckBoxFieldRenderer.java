package es.jcyl.ita.frmdrd.renderer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.ui.form.Field;

public class CheckBoxFieldRenderer extends AbstractFieldRenderer {

    @Override
    public void render(Context context, Field field, ViewGroup parent) {
        String renderCondition = field.getRenderCondition();

        boolean render = true;
        if (StringUtils.isNotEmpty(renderCondition)) {
            render = this.validateCondition(renderCondition);
        }

        if (render) {
            LinearLayout linearLayout = null;

            linearLayout = (LinearLayout) View.inflate(context,
                    R.layout.tool_alphaedit_boolean, null);

            final TextView fieldName = (TextView) linearLayout
                    .findViewById(R.id.field_layout_name);
            final Switch input = (Switch) linearLayout
                    .findViewById(R.id.field_layout_value);


            fieldName.setText(field.getName());

            input.setOnCheckedChangeListener(new OnChangeFieldInterceptor());

            parent.addView(linearLayout);
        }
    }
}
