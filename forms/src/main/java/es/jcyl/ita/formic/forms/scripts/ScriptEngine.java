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


import static es.jcyl.ita.formic.forms.config.DevConsole.error;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    Map<String, String> scriptSources = new HashMap<>();

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
        if (!scripts.containsKey(formId) && scriptSources.containsKey(formId)) {
            storeScript(formId, scriptSources.get(formId));
        }
        return this.scripts.get(formId);
    }

    public void store(String formId, String source) {
        this.scriptSources.put(formId, source);
    }

    private void storeScript(String formId, String source) {
        Script script = rhino.compileString(source, formId, 1, null);
        this.scripts.put(formId, script);
    }

    public void executeScript(Script source) {
        source.exec(rhino, scope);
    }
    public void executeScript(String source) {
        executeScript(source, null);
    }

    public void executeScript(String source, Map<String, Object> params) {
        Script script = rhino.compileString(source, "anonymous", 1, null);
        // put arguments in scope and remove then
        try {
            if(params!=null){
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    ScriptableObject.putProperty(scope, entry.getKey(), Context.javaToJS(entry.getValue(), scope));
                }
            }
            script.exec(rhino, scope);
        } finally {
            if(params!=null){
                for (String key : params.keySet()) {
                    scope.delete(key);
                }
            }
        }
    }

    /**
     * Checks if given name is a function in correct view scope
     *
     * @return
     */
    public boolean isFunction(String method) {
        Object fObj = scope.get(method, scope);
        return (fObj instanceof Function);
    }

    public Object callFunction(String method, Object... args) {
        if (scope == null) {
            throw new IllegalStateException(error("Scope not initialized!. Make sure you put your <script/> " +
                    "tag in current view XML file."));
        }
        // execute function
        Object fObj = scope.get(method, scope);
        if (!(fObj instanceof Function)) {
            throw new IllegalArgumentException("Function not found: " + method);
        }
        Function function = (Function) fObj;
        Object result = function.call(rhino, scope, scope, args);
        return (result instanceof Undefined) ? null : result;
    }

    public Object callFunction(Function function, Object... args) {
        Object result = function.call(rhino, scope, scope, args);
        return (result instanceof Undefined) ? null : result;
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
     * Initializes current scope to remove previous properties and load current View scripts
     */
    public void initScope(String formControllerId) {
        Script script = getScript(formControllerId);
        if (script == null) {
            return; // no js in this view
        }
        this.scope = (ScriptableObject) rhino.newObject(sharedScope);
        scope.setPrototype(sharedScope);
        scope.setParentScope(null);

        script.exec(rhino, scope);
    }

    public ScriptableObject getScope() {
        return scope;
    }

    public void putProperty(String name, Object object) {
        if (scope != null) {
            ScriptableObject.putProperty(scope, name, Context.javaToJS(object, scope));
        }
    }

    public void clearSources() {
        this.scripts.clear();
    }

}
