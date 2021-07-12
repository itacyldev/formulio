package es.jcyl.ita.formic.forms.scripts;
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

import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.view.render.ViewRendererEventHandler;
import es.jcyl.ita.formic.forms.view.render.renderer.WidgetContext;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.repo.Entity;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class RhinoViewRenderHandler implements ViewRendererEventHandler {

    ScriptEngine engine;

    public RhinoViewRenderHandler(ScriptEngine scriptEngine) {
        this.engine = scriptEngine;
    }

    @Override
    public void onViewStart(UIView view) {
    }

    @Override
    public void onEntityContextChanged(Entity entity) {
        engine.putProperty("entity", entity);
    }

    @Override
    public void onWidgetContextChange(WidgetContext context) {
        engine.putProperty("view", context.getViewContext());
    }

    @Override
    public void onBeforeRenderComponent(UIComponent component) {
        if (component == null || StringUtils.isBlank(component.getOnBeforeRenderAction())) {
            return;
        }
        try {
            // get current controller
            engine.callFunction(component.getOnBeforeRenderAction(), component);
        } catch (Exception e) {
            error(String.format("Error while executing onBeforeRenderAction: [%s] in component [%s].",
                    component.getOnBeforeRenderAction(), component.getId()), e);
        }

    }

    @Override
    public void onAfterRenderComponent(Widget widget) {
        UIComponent component = widget.getComponent();
        if (component == null || StringUtils.isBlank(component.getOnAfterRenderAction())) {
            return;
        }
        try {
            engine.callFunction(component.getOnAfterRenderAction(), widget);
        } catch (Exception e) {
            error(String.format("Error while executing onAfterRenderAction: [%s] in component [%s].",
                    component.getOnBeforeRenderAction(), component.getId()), e);
        }
    }

    @Override
    public void onViewEnd(ViewWidget viewWidget) {
        engine.putProperty("root", viewWidget);
    }
}
