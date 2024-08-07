package es.jcyl.ita.formic.forms.config.builders.proxy;
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
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.view.render.ViewRendererEventHandler;
import es.jcyl.ita.formic.forms.view.widget.WidgetContext;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.repo.Entity;

/**
 * Notifies the changes in the widgetContext to the componentProxyFactory so the expression
 * evaluation of components uses the current component context.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ProxyViewRendererEventHandler implements ViewRendererEventHandler {
    private final UIComponentProxyFactory factory;

    public ProxyViewRendererEventHandler(UIComponentProxyFactory factory) {
        this.factory = factory;
    }

    @Override
    public void onViewStart(UIView view) {
        //Do nothing
    }

    @Override
    public void onEntityContextChanged(Entity entity) {
        //Do nothing
    }

    @Override
    public void onWidgetContextChange(WidgetContext context) {
        this.factory.setWidgetContext(context);
    }

    @Override
    public void onBeforeRenderComponent(UIComponent component) {
        //Do nothing
    }

    @Override
    public void onAfterRenderComponent(Widget widget) {
        //Do nothing
    }

    @Override
    public void onViewEnd(ViewWidget viewWidget) {
        //Do nothing
    }
}
