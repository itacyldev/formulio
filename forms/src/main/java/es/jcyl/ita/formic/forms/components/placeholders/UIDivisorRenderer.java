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

import android.view.View;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.Renderer;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class UIDivisorRenderer implements Renderer<UIDivisor> {
    @Override
    public Widget render(RenderingEnv env, UIDivisor component) {
        Widget divisorWidget = ViewHelper.inflate(env.getViewContext(), R.layout.widget_divisor, Widget.class);

        View divisorView = ViewHelper.findViewAndSetId(divisorWidget, R.id.divisor);
        if (component.getColor() != null) {

            divisorView.setBackgroundColor(component.getColor());
        }

        if (component.getStrokeWidth() > 0) {
            divisorView.getLayoutParams().height = component.getStrokeWidth();
        }

        return divisorWidget;
    }
}
