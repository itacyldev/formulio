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
import org.mozilla.javascript.Undefined;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ScriptEngine {
    // TODO: just  prototype to test scripting integration with forms

    /**
     * The Rhino Context object is used to store thread-specific information about the execution
     * environment. There should be one and only one Context associated with each thread that will
     * be executing JavaScript.
     */

    private static ScriptEngine _instance;
    Map<String, Script> scripts = new HashMap<>();
    private Context rhino;
    private ScriptableObject sharedScope;
    private ScriptableObject scope;

    public static ScriptEngine getInstance() {
        if (_instance == null) {
            _instance = new ScriptEngine();
        }
        return _instance;
    }

    public ScriptEngine() {
        this.rhino = Context.enter();
        rhino.setOptimizationLevel(-1);
    }

    public Script getScript(String formId) {
        return this.scripts.get(formId);
    }

    public void store(String formId, String source) {
        Script script = rhino.compileString(source, formId, 1, null);
        this.scripts.put(formId, script);
    }

    public void executeScript(Script source) {
        source.exec(rhino, scope);
    }

    public Map callFunction(String formId, String method, Object... args) {
        Script script = this.scripts.get(formId);
        if (script == null) {
            throw new IllegalStateException("No script found for formId: " + formId);
        }
        if (method == null) {
            throw new IllegalArgumentException("Script method is null!");
        }
        return (script == null) ? null : callFunction(script, method, args);
    }

    public Map callFunction(Script script, String method, Object... args) {
        // load script functions in scope
        script.exec(rhino, scope);
        // execute function
        Function function = (Function) scope.get(method, scope);
        Object call = function.call(rhino, scope, scope, args);
        return (call instanceof Undefined) ? null : (NativeObject) call;
    }

    public void initEngine(Map<String, Object> props) {
        // initialize scope and load Global context
        this.sharedScope = rhino.initStandardObjects();
        if (props != null) {
            for (Map.Entry<String, Object> e : props.entrySet()) {
                ScriptableObject.putProperty(sharedScope, e.getKey(), Context.javaToJS(e.getValue(), sharedScope));
            }
        }
    }

    /**
     * Initializes current scope to remove previous properties
     */
    public void initScope() {
        this.scope = (ScriptableObject) rhino.newObject(sharedScope);
        scope.setPrototype(sharedScope);
        scope.setParentScope(null);
    }

    public void putProperty(String name, Object object) {
        if (scope == null) {
            initScope();
        }
        ScriptableObject.putProperty(scope, name, Context.javaToJS(object, scope));
    }
}
