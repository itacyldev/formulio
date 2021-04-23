package es.jcyl.ita.formic.forms.components.inputfield;

import android.app.AlertDialog;
import android.app.Dialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;

import static android.view.View.inflate;

/*
 * Copyright 2020 Gustavo Río Briones (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Gustavo Río Briones (gustavo.rio@itacyl.es)
 */

public class TextAreaRenderer extends TextFieldRenderer {

    @Override
    protected int getWidgetLayoutId(UIField component) {
        return R.layout.widget_textarea;
    }

    @Override
    protected void composeInputView(RenderingEnv env, InputWidget<UIField, EditText> widget) {

        // configure input view elements
        UIField component = widget.getComponent();
        EditText inputView = widget.getInputView();
        if (component.getInputType() != null) {
            inputView.setInputType(component.getInputType());
        }
        // set floating label
       //setLabel(inputView, textInputLayout, component);

        // set event
        addTextChangeListener(env, inputView, widget);

        TextInputLayout textInputLayout = (TextInputLayout) ViewHelper.findViewAndSetId(widget, R.id.text_input_layout);
        removeUnderline(env, component, textInputLayout);

        TextView label = ViewHelper.findViewAndSetId(widget, R.id.label_view,
                TextView.class);

        // set clear button
        ImageView resetButton = ViewHelper.findViewAndSetId(widget, R.id.field_layout_x,
                ImageView.class);
        if ((Boolean) ConvertUtils.convert(widget.getComponent().isReadOnly(env.getContext()), Boolean.class) || !widget.getComponent().hasDeleteButton()) {
            resetButton.setVisibility(View.GONE);
        }
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                inputView.setText(null);
            }
        });

        // set info button
        ImageView infoButton = ViewHelper.findViewAndSetId(widget, R.id.field_layout_info,
                ImageView.class);
        if (widget.getComponent().getHint() == null) {
            infoButton.setVisibility(View.INVISIBLE);
        }
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(env.getViewContext(), R.style.DialogStyle);
                final View view = inflate(env.getViewContext(), R.layout.info_dialog, null);
                TextView titleView = view.findViewById(R.id.info);
                titleView.setText(component.getHint());
                builder.setCustomTitle(view)
                        .setPositiveButton("OK", null);
                Dialog dialog = builder.create();
                dialog.show();
            }
        });

        setVisibiltyResetButtonLayout(StringUtils.isNotBlank(component.getLabel()), resetButton);

    }



}
