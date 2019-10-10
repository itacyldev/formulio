package es.jcyl.ita.frmdrd.renderer;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.ui.form.Field;

public class TextFieldRenderer extends AbstractFieldRenderer {

    @Override
    public void render(Context context, final Field field, ViewGroup parent) {
        String renderCondition = field.getRenderCondition();

        boolean render = true;
        if (StringUtils.isNotEmpty(renderCondition)) {
            render = this.validateCondition(renderCondition);
        }

        if (render) {
            LinearLayout linearLayout = (LinearLayout) View.inflate(context,
                    R.layout.tool_alphaedit_text, null);

            final TextView fieldName = (TextView) linearLayout
                    .findViewById(R.id.field_layout_name);
            final EditText input = (EditText) linearLayout
                    .findViewById(R.id.field_layout_value);
            final ImageView resetButton = (ImageView) linearLayout
                    .findViewById(R.id.field_layout_x);

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
                    String rerender = field.getRerender();
                }
            });

            fieldName.setText(field.getName());
            parent.addView(linearLayout);
        }
    }


}
