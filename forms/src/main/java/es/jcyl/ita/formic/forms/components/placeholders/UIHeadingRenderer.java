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

public class UIHeadingRenderer extends AbstractRenderer<UIText, Widget<UIText>> {

    @Override
    protected int getWidgetLayoutId(UIText component) {
        return R.layout.widget_heading;
    }

    @Override
    protected void composeWidget(RenderingEnv env, Widget<UIText> widget) {
        UIText heading = widget.getComponent();

        TextView textView = (TextView) ViewHelper.findViewAndSetId(widget, R.id.heading_text);

        setTextAppearance(env, heading, textView);
    }

    protected void setTextAppearance(RenderingEnv env, UIText text, TextView textView){
        if (text.getFontColor() != 0) {
            textView.setTextColor(text.getFontColor());
        }

        textView.setBackgroundColor(text.getBackgroundColor());

        if (text.getFontSize() > 0) {
            textView.setTextSize(text.getFontSize());
        }

        String value = (String) text.getValue(env.getWidgetContext());
        if (text.isUppercase()) {
            value = value.toUpperCase();
        }

        int typeFace = 0;
        if (text.isBold()) {
            typeFace = typeFace + Typeface.BOLD;
        }

        if (text.isItalic()) {
            typeFace = typeFace + Typeface.ITALIC;
        }
        textView.setTypeface(null, typeFace);

        if (text.isUnderlined()) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        textView.setText(value);
    }

}
