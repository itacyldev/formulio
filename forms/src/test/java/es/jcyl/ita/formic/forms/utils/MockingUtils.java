package es.jcyl.ita.formic.forms.utils;
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

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.forms.scripts.ScriptEngine;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;

import static org.mockito.Mockito.*;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class MockingUtils {

    public static MainController mockMainController(Context androidContext) {
        return mockMainController(androidContext, null);
    }

    public static MainController mockMainController(Context androidContext, CompositeContext globalContext) {
        MainController mc = mock(MainController.class);
        when(mc.getFormController()).thenReturn(mock(FormEditController.class));
        // set global context
        when(mc.getGlobalContext()).thenReturn(globalContext);
        // mock and set ActionController
        ActionController mockAc = mock(ActionController.class);
        when(mockAc.getMc()).thenReturn(mc);
        when(mc.getActionController()).thenReturn(mockAc);
        // mock router
        when(mc.getRouter()).thenReturn(mock(Router.class));
        // mock and set Rendering environment
        RenderingEnv env = mock(RenderingEnv.class);
        when(env.getViewContext()).thenReturn(androidContext);
        when(mc.getRenderingEnv()).thenReturn(env);

        // create script engine and set to Mc
        Map<String, Object> props = new HashMap<>();
        if (globalContext != null) {
            props.put("ctx", globalContext);
        }
        props.put("renderEnv", env);
        props.put("console", new DevConsole());
        ScriptEngine scriptEngine = new ScriptEngine();
        scriptEngine.initEngine(props);
        when(mc.getScriptEngine()).thenReturn(scriptEngine);

        return mc;
    }
}
