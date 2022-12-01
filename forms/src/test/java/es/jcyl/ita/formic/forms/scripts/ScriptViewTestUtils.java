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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.utils.dummy.DummyWidgetContextHolder;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnvFactory;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class ScriptViewTestUtils {

    public static RenderingEnv createViewMock(String componentIdPrefix, ScriptEngine engine) {
        // create 5 widgetContext, each context has a widget it its View context with Id "myproperty"
        Map<String, WidgetContextHolder> map = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            String id = componentIdPrefix + "-" + i;
            WidgetContextHolder wCtx = new DummyWidgetContextHolder(id);
            InputWidget widget = mock(InputWidget.class);
            when(widget.getComponentId()).thenReturn(id);
            UIInputComponent component = mock(UIInputComponent.class);
            when(component.getId()).thenReturn(id);
            when(widget.getComponent()).thenReturn(component);
            when(widget.getValue()).thenReturn(RandomUtils.randomInt(0,100));
            wCtx.getWidgetContext().getViewContext().registerWidget(widget);
            map.put(id, wCtx);
        }
        ActionController mcAC = mock(ActionController.class);
        RenderingEnvFactory.getInstance().setActionController(mcAC);
        RenderingEnv env = RenderingEnvFactory.getInstance().create();
        env.setScriptEngine(engine);
        // set the root view Widget
        ViewWidget viewWidget = mock(ViewWidget.class);
        when(viewWidget.getContextHoldersMap()).thenReturn(map);
        when(viewWidget.getContextHolders()).thenReturn(map.values());
        env.setRootWidget(viewWidget);
        return env;
    }
}
