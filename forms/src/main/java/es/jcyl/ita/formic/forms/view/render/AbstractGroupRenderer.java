package es.jcyl.ita.formic.forms.view.render;
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

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHelper;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/**
 * Base class with default implementations to avoid boiler plate code.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public abstract class AbstractGroupRenderer<C extends UIComponent, W extends Widget<C>>
        extends AbstractRenderer<C, W> implements GroupRenderer<C> {

    @Override
    public void initGroup(RenderingEnv env, Widget<C> root) {
    }

    @Override
    public void addViews(RenderingEnv env, Widget<C> root, View[] views) {
        for (View view : views) {
            root.addView(view);
        }
    }

    @Override
    public void endGroup(RenderingEnv env, Widget<C> root) {
        // check if any of the nested elements has an error message
        if (WidgetContextHelper.hasNestedMessages(env.getWidgetContext(), root.getComponent())) {
            WidgetContextHelper.setMessage(env.getWidgetContext(), root.getComponent().getId(), "error");
            setNestedMessage(env, root);
        }
    }

    protected void setNestedMessage(RenderingEnv env, Widget<C> widget) {
    }
}
