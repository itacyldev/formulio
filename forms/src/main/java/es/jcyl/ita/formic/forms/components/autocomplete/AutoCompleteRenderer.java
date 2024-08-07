package es.jcyl.ita.formic.forms.components.autocomplete;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
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

import android.view.View;
import android.widget.ImageView;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.InputTextRenderer;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Creates view elements for autocomplete component
 */
public class AutoCompleteRenderer extends InputTextRenderer<UIAutoComplete, AutoCompleteView> {

    @Override
    protected AutoCompleteWidget createWidget(RenderingEnv env, UIAutoComplete component) {
        AutoCompleteWidget widget = (AutoCompleteWidget) super.createWidget(env, component);
        return widget;
    }

    @Override
    protected void composeInputView(RenderingEnv env, InputWidget<UIAutoComplete, AutoCompleteView> widget) {
        AutoCompleteView input = widget.getInputView();
        ImageView arrowDropDown = ViewHelper.findViewAndSetId(widget, R.id.input_view_image,
                ImageView.class);
        input.initialize(env, widget, arrowDropDown);

        arrowDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.showDropDown();
            }
        });

        setOnClickListenerResetButton(widget.getComponent(), input);


    }

    private void setOnClickListenerResetButton(UIAutoComplete component, AutoCompleteView input) {
        ImageView resetButton = component.getResetButton();
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                input.setSelection(-1);
            }
        });
    }

    @Override
    protected int getWidgetLayoutId(UIAutoComplete component) {
        return R.layout.widget_autocomplete;
    }
}
