package es.jcyl.ita.formic.forms.components.inputfield;

import android.view.View;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;

import static com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT;

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

        TextInputLayout textInputLayout = (TextInputLayout) ViewHelper.findViewAndSetId(widget, R.id.text_input_layout);
        // set floating label
        setLabel(textInputLayout, component);

        // set event
        addTextChangeListener(env, inputView, component);

        // set clear button
        textInputLayout.setEndIconActivated(true);
        textInputLayout.setEndIconMode(END_ICON_CLEAR_TEXT);
        textInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputView.setText("");
            }
        });
    }

}
