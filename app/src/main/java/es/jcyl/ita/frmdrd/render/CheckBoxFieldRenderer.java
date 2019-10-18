package es.jcyl.ita.frmdrd.render;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.configuration.DataBinder;
import es.jcyl.ita.frmdrd.ui.form.UIField;
import es.jcyl.ita.frmdrd.ui.form.UIComponent;

public class CheckBoxFieldRenderer extends AbstractFieldRenderer {

    public CheckBoxFieldRenderer() {
        super();
    }

    @Override
    public void render(Context context, UIComponent field, ViewGroup parent) {
        String renderCondition = field.getRenderCondition();

        boolean render = true;
        if (StringUtils.isNotEmpty(renderCondition)) {
            render = this.validateCondition(renderCondition);
        }

        if (render) {
            LinearLayout linearLayout = null;

            linearLayout = (LinearLayout) View.inflate(context,
                    R.layout.tool_alphaedit_boolean, null);

            final TextView fieldLabel = (TextView) linearLayout
                    .findViewById(R.id.field_layout_name);
            final Switch input = (Switch) linearLayout
                    .findViewById(R.id.field_layout_value);


            fieldLabel.setText(field.getLabel());

            input.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton,
                                             boolean value) {
                    onChangeInterceptor.onChange((UIField)field, value);
                }
            });
            DataBinder.registerBinding(input.hashCode(), field);
            parent.addView(linearLayout);

        }
    }

    @Override
    public void render(int viewId, UIComponent field) {

    }
}
