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

import androidx.test.internal.util.ReflectionUtil;

import org.mockito.internal.util.reflection.FieldSetter;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;

import static org.mockito.Mockito.*;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class MockingUtils {

//
//    public static ActionController mockAC(){
//        ActionController mockAC = mock(ActionController.class);
//        return mockAC;
//    }

    public static MainController mockMainController(Context ctx) {
        MainController mc = mock(MainController.class);
        when(mc.getFormController()).thenReturn(mock(FormEditController.class));
        when(mc.getRouter()).thenReturn(mock(Router.class));
        RenderingEnv env = mock(RenderingEnv.class);
        when(env.getViewContext()).thenReturn(ctx);
        when(mc.getRenderingEnv()).thenReturn(env);
        return mc;
    }

}
