package es.jcyl.ita.formic.forms.components.view;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.view.render.AbstractGroupRenderer;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;

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
public class UIViewRenderer extends AbstractGroupRenderer<UIView, Widget<UIView>> {

    @Override
    protected void composeWidget(RenderingEnv env, Widget<UIView> widget) {
        // Do nothing
    }

    @Override
    protected int getWidgetLayoutId(UIView component) {
        return R.layout.widget_view;
    }
}