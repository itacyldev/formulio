package es.jcyl.ita.frmdrd.render;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.lifecycle.Lifecycle;
import es.jcyl.ita.frmdrd.ui.form.UIField;

public class CheckBoxFieldRenderer extends AbstractFieldRenderer {

    public CheckBoxFieldRenderer(Lifecycle lifecycle) {
        super(lifecycle);
    }

    @Override
    public View render(Context context, UIField field) {
        String renderCondition = field.getRenderCondition();

        boolean render = true;
        if (StringUtils.isNotEmpty(renderCondition)) {
            render = this.validateCondition(renderCondition);
        }


        LinearLayout linearLayout = null;

        linearLayout = (LinearLayout) View.inflate(context,
                R.layout.tool_alphaedit_boolean, null);

        final TextView fieldLabel = linearLayout
                .findViewById(R.id.field_layout_name);
        fieldLabel.setTag("label");
        final Switch input = linearLayout
                .findViewById(R.id.field_layout_value);
        input.setTag("input");


        fieldLabel.setText(field.getLabel());

        input.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton,
                                         boolean value) {
                onChangeInterceptor.onChange(field.getId());
            }
        });

        linearLayout.setVisibility(render ? View.VISIBLE : View.INVISIBLE);

        bindField(field, input);

        return linearLayout;
    }


}
