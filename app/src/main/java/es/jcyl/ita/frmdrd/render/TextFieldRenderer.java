package es.jcyl.ita.frmdrd.render;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.lifecycle.Lifecycle;
import es.jcyl.ita.frmdrd.ui.form.UIField;

public class TextFieldRenderer extends AbstractFieldRenderer {

    public TextFieldRenderer(Lifecycle lifecycle) {
        super(lifecycle);
    }

    @Override
    public View render(Context context, final UIField field) {
        String renderCondition = field.getRenderCondition();

        boolean render = true;
        if (StringUtils.isNotEmpty(renderCondition)) {
            render = this.validateCondition(renderCondition);
        }


        LinearLayout linearLayout = (LinearLayout) View.inflate(context,
                R.layout.tool_alphaedit_text, null);

        final TextView fieldLabel = (TextView) linearLayout
                .findViewById(R.id.field_layout_name);
        fieldLabel.setText(field.getLabel());
        fieldLabel.setTag("label");
        final EditText input = (EditText) linearLayout
                .findViewById(R.id.field_layout_value);
        input.setTag("input");

        final ImageView resetButton = (ImageView) linearLayout
                .findViewById(R.id.field_layout_x);
        resetButton.setTag("reset");

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                onChangeInterceptor.onChange(field.getId());
            }
        });


        linearLayout.setVisibility(render ? View.VISIBLE : View.INVISIBLE);

        bindField(field, linearLayout);


        return linearLayout;

    }



}
