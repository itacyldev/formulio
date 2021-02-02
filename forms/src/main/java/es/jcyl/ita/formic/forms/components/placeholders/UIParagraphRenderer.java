package es.jcyl.ita.formic.forms.components.placeholders;

/*
 * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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

import android.widget.TextView;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.AbstractRenderer;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class UIParagraphRenderer extends AbstractRenderer<UIParagraph, Widget<UIParagraph>> {

    @Override
    protected int getWidgetLayoutId(UIParagraph component) {
        return R.layout.widget_paragraph;
    }

    @Override
    protected void composeWidget(RenderingEnv env, Widget<UIParagraph> widget) {
        UIParagraph paragraph = widget.getComponent();

        String value = (String) paragraph.getValue(env.getContext());

        TextView textView = (TextView) ViewHelper.findViewAndSetId(widget, R.id.paragraph_text);

        if (paragraph.getNumLines() > 0) {
            textView.setLines(paragraph.getNumLines());
        } 
        textView.setText(value);

    }


}
