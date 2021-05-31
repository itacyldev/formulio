package es.jcyl.ita.formic.forms.components.datalist;
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

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.LinearLayout;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.view.render.AbstractGroupRenderer;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;


/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class UIDatalistRenderer extends AbstractGroupRenderer<UIDatalist, DatalistWidget> {
    @Override
    protected void composeWidget(RenderingEnv env, DatalistWidget widget) {

    }

    @Override
    protected int getWidgetLayoutId(UIDatalist component) {
        return R.layout.widget_datalist;
    }

    @Override
    protected DatalistWidget createWidget(RenderingEnv env, UIDatalist component) {
        DatalistWidget widget = super.createWidget(env, component);
        widget.setTag(getWidgetViewTag(component));
        return widget;
    }

    @Override
    protected void setupWidget(RenderingEnv env, DatalistWidget widget) {
        super.setupWidget(env, widget);

        LinearLayout contentView = widget.findViewById(R.id.datalist_content_layout);

        widget.setContentView(contentView);
        widget.load(env);
    }

    @SuppressLint("ResourceType")
    @Override
    public void addViews(RenderingEnv env, Widget<UIDatalist> root, View[] views) {
        for (View view : views) {
            ((DatalistWidget) root).getContentView().addView(view);
        }
    }
}
