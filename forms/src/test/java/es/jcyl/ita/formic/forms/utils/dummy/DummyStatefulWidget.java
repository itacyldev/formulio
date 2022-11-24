package es.jcyl.ita.formic.forms.utils.dummy;
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

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.view.render.renderer.WidgetContext;
import es.jcyl.ita.formic.forms.view.widget.StatefulWidget;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class DummyStatefulWidget implements StatefulWidget {
    Object state;
    Widget widget;
    WidgetContext widgetContext;
    UIComponent component;

    @Override
    public UIComponent getComponent() {
        return component;
    }

    @Override
    public String getComponentId() {
        return component.getId();
    }

    public void setComponent(UIComponent component) {
        this.component = component;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public Widget getWidget() {
        return widget;
    }

    public void setWidget(Widget widget) {
        this.widget = widget;
    }

    @Override
    public WidgetContext getWidgetContext() {
        return widgetContext;
    }

    @Override
    public WidgetContextHolder getHolder() {
        return null;
    }

    public void setWidgetContext(WidgetContext widgetContext) {
        this.widgetContext = widgetContext;
    }

    @Override
    public Object getState() {
        return state;
    }

    @Override
    public boolean allowsPartialRestore() {
        return false;
    }

    @Override
    public void setState(Object state) {
        this.state = state;
    }
}
