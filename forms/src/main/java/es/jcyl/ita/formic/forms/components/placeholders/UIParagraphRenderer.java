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

public class UIParagraphRenderer extends AbstractRenderer<UIParagraph, Widget<UIParagraph>> {

    @Override
    protected int getWidgetLayoutId(UIParagraph component) {
        return R.layout.widget_paragraph;
    }

    @Override
    protected void composeWidget(RenderingEnv env, Widget<UIParagraph> widget) {
        UIParagraph paragraph = (UIParagraph) widget.getComponent();

        TextView textView = (TextView) ViewHelper.findViewAndSetId(widget, R.id.paragraph_text);

        setTextAppearance(env, paragraph, textView);

        if (paragraph.getNumLines() > 0) {
            textView.setLines(paragraph.getNumLines());
        } 

    }

    private void setTextAppearance(RenderingEnv env, UIParagraph paragraph, TextView textView){
        if (paragraph.getFontColor() != 0) {
            textView.setTextColor(paragraph.getFontColor());
        }

        textView.setBackgroundColor(paragraph.getBackgroundColor());

        if (paragraph.getFontSize() > 0) {
            textView.setTextSize(paragraph.getFontSize());
        }

        String value = (String) paragraph.getValue(env.getWidgetContext());
        if (paragraph.isUppercase()) {
            value = value.toUpperCase();
        }

        int typeFace = 0;
        if (paragraph.isBold()) {
            typeFace = typeFace + Typeface.BOLD;
        }

        if (paragraph.isItalic()) {
            typeFace = typeFace + Typeface.ITALIC;
        }
        textView.setTypeface(null, typeFace);

        if (paragraph.isUnderlined()) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        textView.setText(value);
    }


}
