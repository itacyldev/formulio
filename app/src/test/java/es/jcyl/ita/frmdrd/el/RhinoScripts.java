package es.jcyl.ita.frmdrd.el;
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

import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.crtrepo.context.impl.BasicContext;
import es.jcyl.ita.crtrepo.test.utils.TestUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class RhinoScripts {
    private static final String RHINO_LOG = "var log = Packages.io.vec.ScriptAPI.log;";

    public static void log(String msg) {
        android.util.Log.i("RHINO_LOG", msg);
    }

    @Test
    public void testBasicRhino() throws Exception {
        Context rhino = Context.enter();
        // Turn off optimization to make Rhino Android compatible
        rhino.setOptimizationLevel(-1);

        Scriptable scope = rhino.initStandardObjects();
        Object result = rhino.evaluateString(scope, "var a = 1; a+2;", "<cmd>", 1, null);
        System.out.println(result);

        Context.exit();
    }


    @Test
    public void testScriptFromFile() throws Exception {
        File file = TestUtils.findFile("scripts/rhino1.js");
        String source = TestUtils.readSource(file);

        Context rhino = Context.enter();
        rhino.setOptimizationLevel(-1);
        Scriptable scope = rhino.initStandardObjects();

        // put a context object in the scope
        BasicContext ctx = new BasicContext("");
        ctx.put("varStr", "cccccc");
        ctx.put("varLong",22);
        ctx.put("varDate", new Date());
//        scope.put("ctx",ctx);
//        ScriptableObject.putProperty(scope, "ctx", ctx);
        ScriptableObject.putProperty(scope, "out", Context.javaToJS(System.out, scope));
        ScriptableObject.putProperty(scope, "ctx", Context.javaToJS(ctx, scope));
        ScriptableObject.putProperty(scope, "str", "asdfasfa");

        Script script = rhino.compileString(source, "src", 1, null);

        script.exec(rhino, scope);

//        rhino.evaluateString(scope, source, "src", 1, null);

        // We get the hello function defined in JavaScript
        String functionName ="hello";
        Function function = (Function) scope.get(functionName, scope);

        // Call the hello function with params
        Object[] functionParams = new Object[] {"go go go!!!."};
        NativeObject result = (NativeObject) function.call(rhino, scope, scope, functionParams);
        System.out.println(result.get("foo"));
        System.out.println(result.get("other"));
    }


}
