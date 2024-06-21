package es.jcyl.ita.formic.forms.view.render;
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
 * Extends input renderer to include common methods for renderers that support textview based
 * widgets.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.view.render.renderer.MessageHelper;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;


public abstract class InputTextRenderer<C extends UIInputComponent, I extends TextView>
        extends InputRenderer<C, I> {

    protected static final Object EMPTY_STRING = "";

    @Override
    protected void setMessages(RenderingEnv env, InputWidget<C, I> widget) {
        UIInputComponent component = widget.getComponent();
        String message = MessageHelper.getMessage(env, component);
        if (message != null) {
            widget.getInputView().setError(message);
        }
    }

    protected void setVisibilityResetButtonLayout(boolean hasLabel, ImageView resetButton){
        if ((resetButton.getVisibility() == View.INVISIBLE || resetButton.getVisibility() == View.GONE) && !hasLabel){
            LinearLayout linearLayout = (LinearLayout) resetButton.getParent();
            linearLayout.setVisibility(View.GONE);
        }
    }

}
