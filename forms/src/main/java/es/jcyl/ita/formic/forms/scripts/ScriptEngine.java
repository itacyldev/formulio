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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    Map<String, List<Script>> scripts = new HashMap<>();
    Map<String, List<ScriptRef>> scriptSources = new HashMap<>();

    private Context rhino;
    private ScriptableObject sharedScope;
    private ScriptableObject scope;
    private ScriptRefReader sourceReader;

    public static ScriptEngine getInstance() {
        if (_instance == null) {
            _instance = new ScriptEngine();
        }
        return _instance;
    }

    public ScriptEngine() {
        initContext();
    }

    //////////////////////////////
    // SCRIPTING CONTEXT MANAGEMENT
    //////////////////////////////

    public void initContext() {
        this.rhino = Context.enter();
        rhino.setOptimizationLevel(-1);
        this.sourceReader = new ScriptRefReader();
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
        this.scope = (ScriptableObject) rhino.newObject(sharedScope);
        scope.setPrototype(sharedScope);
        scope.setParentScope(null);

        List<Script> scripts = getScripts(formControllerId);
        if (scripts != null) {
            for (Script script : scripts) {
                script.exec(rhino, scope);
            }
        }
    }

    /**
     * Just for testing purposes
     *
     * @return
     */
    public ScriptableObject getScope() {
        return scope;
    }

    public void putProperty(String name, Object object) {
        if (scope != null) {
            ScriptableObject.putProperty(scope, name, Context.javaToJS(object, scope));
        }
    }

    ///////////////////////////////
    /// SCRIPT EXECUTION
    ///////////////////////////////

    public void executeScript(Script source) {
        source.exec(rhino, scope);
    }

    public void executeScript(String source) {
        executeScript(source, null);
    }

    public void executeScript(String source, Map<String, Object> params) {
        Script script = rhino.compileString(source, "anonymous", 1, null);
        // put arguments in scope and remove then after execution
        try {
            if (params != null) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    ScriptableObject.putProperty(scope, entry.getKey(), Context.javaToJS(entry.getValue(), scope));
                }
            }
            script.exec(rhino, scope);
        } finally {
            if (params != null) {
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
            throw new ScriptEngineException(error("Scope not initialized!. Make sure you put your <script/> " +
                    "tag in current view XML file."));
        }
        // execute function
        Object fObj = scope.get(method, scope);
        if (!(fObj instanceof Function)) {
            throw new ScriptEngineException("Function not found: " + method);
        }
        Function function = (Function) fObj;
        Object result = function.call(rhino, scope, scope, args);
        return (result instanceof Undefined) ? null : result;
    }

    public Object callFunction(Function function, Object... args) {
        Object result = function.call(rhino, scope, scope, args);
        return (result instanceof Undefined) ? null : result;
    }

    //////////////////////////////////////////
    // MANAGE SOURCES AND SCRIPTS
    //////////////////////////////////////////

    public void clearSources() {
        for (List lst : this.scriptSources.values()) {
            lst.clear();
        }
        this.scriptSources.clear();
        for (List lst : this.scripts.values()) {
            lst.clear();
        }
        this.scripts.clear();
    }

    public List<Script> getScripts(String formId) {
        // check if source is already compiled
        if (!scripts.containsKey(formId) && scriptSources.containsKey(formId)) {
            for (ScriptRef srcRef : scriptSources.get(formId)) {
                try {
                    String source = sourceReader.readSource(srcRef);
                    Script script = storeScript(formId, source);
                    srcRef.bindScript(script);
                } catch (Exception e) {
                    throw new ScriptEngineException("Error while trying to reade script reference: " + srcRef, e);
                }
            }
        }
        return this.scripts.get(formId);
    }

    public void store(String formId, ScriptRef source) {
        if (!this.scriptSources.containsKey(formId)) {
            this.scriptSources.put(formId, new ArrayList<>());
        }
        this.scriptSources.get(formId).add(source);
    }

    private Script storeScript(String formId, String source) {
        Script script = rhino.compileString(source, formId, 1, null);
        if (!this.scripts.containsKey(formId)) {
            this.scripts.put(formId, new ArrayList<>());
        }
        this.scripts.get(formId).add(script);
        return script;
    }

    /**
     * Finds all the scripts that are related the forms defined in the given file and removes the sources and the
     * compiled scripts.
     *
     * @param formFilePath
     */
    public void clearScriptsByFormFile(String formFilePath) {
        // stores the form Ids whose scripts we have to wipe
        Set<String> formIds = new HashSet<>();
        // find all the references which origin is the given file
        for (Map.Entry<String, List<ScriptRef>> entry : scriptSources.entrySet()) {
            for (ScriptRef ref : entry.getValue()) {
                if (formFilePath.endsWith(ref.getOrigin())) {
                    // this formId is defined in the formFilePath, collect the id and go form next formId
                    formIds.add(entry.getKey());
                    break;
                }
            }
        }
        // remove the sources and script for the collected Ids
        for (String formId : formIds) {
            clearScriptsByFormId(formId);
        }
    }

    public void clearScriptsByFormId(String formId) {
        List<ScriptRef> removedSources = this.scriptSources.remove(formId);
        if (removedSources != null) {
            removedSources.clear();
        }
        List<Script> removedScripts = this.scripts.remove(formId);
        if (removedScripts != null) {
            removedScripts.clear();
        }
    }

    /**
     * Finds the scriptRefs created with the given file and reload its scripts
     *
     * @param jsFilePath: js file absolute path
     */
    public void reloadScriptFile(String jsFilePath) {
        // read file Source
        String newSource = null;
        try {
            newSource = this.sourceReader.readSource(ScriptRef.createSourceFileScriptRef(jsFilePath, null));
        } catch (IOException e) {
            throw new ScriptEngineException("Error while trying to read script file " + jsFilePath, e);
        }

        // find all the scriptRefs that points the the given file and reload them
        for (Map.Entry<String, List<ScriptRef>> entry : scriptSources.entrySet()) {
            String formId = entry.getKey();
            for (ScriptRef ref : entry.getValue()) {
                if (ref.isSourceFile() && jsFilePath.endsWith(ref.getFilePath())) {
                    // find the scripts bound to the scriptRefs, recompile and replace the
                    // references for the given formId
                    Script newScript = rhino.compileString(newSource, formId, 1, null);
                    replaceScript(formId, ref.boundScript(), newScript);
                    // bind the script ref to the new script
                    ref.bindScript(newScript);
                }
            }
        }
    }

    /**
     * Given a js file path, finds the formIds that references the passed file.
     *
     * @param jsFilePath
     * @return
     */
    public Set<String> findDependantForms(String jsFilePath) {
        Set<String> formIds = new HashSet<>();
        // find all the scriptRefs that points the the given file and reload them
        for (Map.Entry<String, List<ScriptRef>> entry : scriptSources.entrySet()) {
            String formId = entry.getKey();
            for (ScriptRef ref : entry.getValue()) {
                if (ref.isSourceFile() && jsFilePath.endsWith(ref.getFilePath())) {
                    formIds.add(formId);
                }
            }
        }
        return formIds;
    }

    /**
     * Replace the given script in the list of scripts of the passed formId
     *
     * @param formId
     * @param scriptToReplace
     * @param newScript
     */
    private void replaceScript(String formId, Script scriptToReplace, Script newScript) {
        List<Script> previousScripts = this.scripts.remove(formId);
        List<Script> newScripts = new ArrayList<>();
        // add new script
        newScripts.add(newScript);
        // add other scripts referenced from the view
        if (previousScripts != null) {
            for (Script existingScript : previousScripts) {
                if (existingScript != scriptToReplace) {
                    newScripts.add(existingScript);
                }
            }
        }
        this.scripts.put(formId, newScripts);
    }

}
