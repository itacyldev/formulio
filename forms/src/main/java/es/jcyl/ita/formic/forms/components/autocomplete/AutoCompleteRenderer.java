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

import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.InputTextRenderer;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Creates view elements for autocomplete component
 */
public class AutoCompleteRenderer extends InputTextRenderer<UIAutoComplete, AutoCompleteView> {

    @Override
    protected void composeInputView(RenderingEnv env, InputWidget<UIAutoComplete, AutoCompleteView> widget) {
        AutoCompleteView input = widget.getInputView();
        input.initialize(env, widget.getComponent());
        input.load(env);

        ImageView resetButton = ViewHelper.findViewAndSetId(widget, R.id.field_layout_x,
                ImageView.class);
        if (widget.getComponent().isReadOnly() || !widget.getComponent().hasDeleteButton()) {
            resetButton.setVisibility(View.INVISIBLE);
        }
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                input.setSelection(-1);
            }
        });

        setVisibiltyResetButtonLayout(StringUtils.isNotBlank(widget.getComponent().getLabel()), resetButton);
    }

    @Override
    protected int getWidgetLayoutId(UIAutoComplete component) {
        return R.layout.widget_autocomplete;
    }
}
