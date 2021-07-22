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

import android.graphics.Paint;
import android.graphics.Typeface;
import android.widget.TextView;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.AbstractRenderer;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class UIHeadingRenderer extends AbstractRenderer<UIHeading, Widget<UIHeading>> {

    @Override
    protected int getWidgetLayoutId(UIHeading component) {
        return R.layout.widget_heading;
    }

    @Override
    protected void composeWidget(RenderingEnv env, Widget<UIHeading> widget) {
        UIHeading heading = widget.getComponent();

        String value = (String) heading.getValue(env.getWidgetContext());

        TextView textView = (TextView) ViewHelper.findViewAndSetId(widget, R.id.heading_text);

        if (heading.getFontColor() != 0) {
            textView.setTextColor(heading.getFontColor());
        }

        textView.setBackgroundColor(heading.getBackgroundColor());

        if (heading.getFontSize() > 0) {
            textView.setTextSize(heading.getFontSize());
        }

        if (heading.isUppercase()) {
            value = value.toUpperCase();
        }

        int typeFace = 0;
        if (heading.isBold()) {
            typeFace = typeFace + Typeface.BOLD;
        }

        if (heading.isItalic()) {
            typeFace = typeFace + Typeface.ITALIC;
        }
        textView.setTypeface(null, typeFace);

        if (heading.isUnderlined()) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        textView.setText(value);
    }
}
