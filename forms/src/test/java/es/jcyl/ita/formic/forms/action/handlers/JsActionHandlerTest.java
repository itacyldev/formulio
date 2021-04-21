package es.jcyl.ita.formic.forms.action.handlers;
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

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.actions.ActionContext;
import es.jcyl.ita.formic.forms.actions.ActionType;
import es.jcyl.ita.formic.forms.actions.JsActionHandler;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.scripts.ScriptEngine;
import es.jcyl.ita.formic.forms.utils.MockingUtils;

import static org.hamcrest.Matchers.*;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class JsActionHandlerTest {
    static Context ctx;

    @Before
    public void setUp() {
        if (ctx != null) {
            ctx = InstrumentationRegistry.getInstrumentation().getContext();
        }
        Config.init(ctx, "");
    }

    private static final String JS_SOURCE =
            "function myJsFunction(){\n" +
                    "	out.add('someValue');\n" +
                    "   console.log(out);\n"+
                    "}";

    @Test
    public void testExecuteJsFunction() throws Exception {
        // mock main controller and prepare action controller
        MainController mc = MockingUtils.mockMainController(ctx);

        // prepare user Action
        UserAction userAction = new UserAction(ActionType.JS);
        Map<String, Serializable> params = new HashMap<>();
        params.put("method", "myJsFunction");
        userAction.setParams(params);

        // create engine and store js function
        ScriptEngine scriptEngine = mc.getScriptEngine();
        scriptEngine.store(mc.getFormController().getId(), JS_SOURCE);
        scriptEngine.initScope();

        List<String> out = new ArrayList();
        scriptEngine.putProperty("out", out);
        // act - execute action
        JsActionHandler handler = new JsActionHandler(mc, mc.getRouter());
        handler.handle(new ActionContext(mc.getFormController(), ctx), userAction);

        Assert.assertThat(out, hasSize(1));
    }
}