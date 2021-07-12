package es.jcyl.ita.formic.forms.view.widget;
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

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.renderer.WidgetContext;

/**
 * Base class to implement view components.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class Widget<C extends UIComponent> extends LinearLayout {

    protected C component;
    private ViewWidget rootWidget;
    private WidgetContext widgetContext;

    public Widget(Context context) {
        super(context);
    }

    public Widget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Widget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Widget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public String getComponentId() {
        return this.component.getId();
    }

    public C getComponent() {
        return component;
    }

    public void setComponent(C component) {
        this.component = component;
    }

    public void setup(RenderingEnv env) {
    }
    public boolean isVisible() {
        return this.getVisibility() == VISIBLE;
    }

    public ViewWidget getRootWidget() {
        return rootWidget;
    }

    public void setRootWidget(ViewWidget getRootWidget) {
        this.rootWidget = getRootWidget;
    }

    public WidgetContext getWidgetContext() {
        return widgetContext;
    }

    public void setWidgetContext(WidgetContext widgetContext) {
        this.widgetContext = widgetContext;
    }
}
