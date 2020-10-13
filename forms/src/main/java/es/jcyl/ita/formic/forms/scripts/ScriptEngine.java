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


import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ScriptEngine {
    // TODO: just  prototype to test scripting integration with forms
    private static ScriptEngine _instance;
    Map<String, Script> scripts = new HashMap<>();

    public static ScriptEngine getInstance() {
        if (_instance == null) {
            _instance = new ScriptEngine();
        }
        return _instance;
    }

    public void store(String formId, String source) {
        Context rhino = Context.enter();
        Script script = rhino.compileString(source, formId, 1, null);
        this.scripts.put(formId, script);
    }

    public Map execute(String formId, es.jcyl.ita.formic.core.context.Context ctx, String method, Object... args) {
        Script script = scripts.get(formId);

        Context rhino = Context.enter();
        rhino.setOptimizationLevel(-1);
        // initialize scope and load Global context
        Scriptable scope = rhino.initStandardObjects();
        ScriptableObject.putProperty(scope, "out", Context.javaToJS(System.out, scope));
        ScriptableObject.putProperty(scope, "ctx", Context.javaToJS(ctx, scope));

        // load script functions in scope
        script.exec(rhino, scope);
        // execute function
        Function function = (Function) scope.get(method, scope);
        NativeObject result = (NativeObject) function.call(rhino, scope, scope, args);
        return result;
    }
}
