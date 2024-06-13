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
import es.jcyl.ita.formic.forms.view.widget.WidgetContext;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */

public class DummyWidgetContextHolder implements WidgetContextHolder {
    private String holderId;
    Widget widget ;
    UIComponent component;
    WidgetContext widgetContext;

    public DummyWidgetContextHolder(String holderId){
        this.holderId = holderId;
        widgetContext = new WidgetContext(this);
    }

    @Override
    public String getHolderId() {
        return holderId;
    }

    public void setHolderId(String holderId) {
        this.holderId = holderId;
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
    public UIComponent getComponent() {
        return component;
    }

    @Override
    public String getComponentId() {
        return null;
    }

    public void setComponent(UIComponent component) {
        this.component = component;
    }

    @Override
    public WidgetContextHolder getHolder() {
        return this;
    }

    @Override
    public void dispose() {

    }


    @Override
    public WidgetContext getWidgetContext() {
        return widgetContext;
    }

    @Override
    public void setWidgetContext(WidgetContext widgetContext) {
        this.widgetContext = widgetContext;
    }
}