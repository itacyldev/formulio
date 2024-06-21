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

import org.mini2Dx.beanutils.ConvertUtils;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JsDevTools;
import org.mozilla.javascript.NativeJavaObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Extends ArrayList to provide filter-map-reduce pattern on Java Lists to simplify access to objects from js functions.
 *
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class ScriptableList<T> extends ArrayList<T> {
    private final ScriptEngine engine;

    public ScriptableList(ScriptEngine engine) {
        this.engine = engine;
    }

    /**
     * Map patter function to transform ScriptableList into a new Scriptable list applying the given
     * function to current elements.
     *
     * @param object
     */
    public ScriptableList<Object> map(Object object) {
        checkFunctionParameter(object, "flat()");
        ScriptableList<Object> resultList = new ScriptableList<>(this.engine);
        for (Object item : this) {
            Object result = engine.callFunction((Function) object, item);
            if (result == null) {
                throw new IllegalArgumentException("The callback function passed to map() returned a null value. " +
                        JsDevTools.decompile((Function) object));
            }
            if (result instanceof NativeJavaObject) {
                result = ((NativeJavaObject) result).unwrap();
            }
            resultList.add(result);
        }
        return resultList;
    }

    /**
     * Creates a new List applying the callback function on current elements and flatting found collections and maps
     * into the resulting list.
     *
     * @param object
     */
    public ScriptableList<Object> flatMap(Object object) {
        checkFunctionParameter(object, "flatMap()");
        ScriptableList<Object> resultList = new ScriptableList<>(this.engine);
        for (Object item : this) {
            Object result = engine.callFunction((Function) object, item);
            if (result == null) {
                throw new IllegalArgumentException("The callback function passed to map() returned a null value. " +
                        JsDevTools.decompile((Function) object));
            }
            if (result instanceof NativeJavaObject) {
                result = ((NativeJavaObject) result).unwrap();
            }
            if (result instanceof Collection) {
                resultList.addAll((Collection) result);
            } else if (result instanceof Map) {
                resultList.addAll(((Map) result).values());
            } else {
                resultList.add(result);
            }
        }
        return resultList;
    }

    /**
     * Applies the given function to all the elements of current list. If the function returns an object it is add to the result list.
     *
     * @param object
     */
    public ScriptableList<Object> apply(Object object) {
        checkFunctionParameter(object, "apply()");
        ScriptableList<Object> resultList = new ScriptableList<>(this.engine);
        for (Object item : this) {
            Object result = engine.callFunction((Function) object, item);
            if (result != null) {
                if (result instanceof NativeJavaObject) {
                    result = ((NativeJavaObject) result).unwrap();
                }
                resultList.add(result);
            }
        }
        return resultList;
    }

    /**
     * Applies the given function to current list to create a sublist
     * with items that meet the condition given by the function.
     *
     * @param object
     * @return
     */
    public ScriptableList<Object> filter(Object object) {
        return (ScriptableList<Object>) doFilter(object, false);
    }

    /**
     * Same as filter() method but returns the first element that meets the condition or null
     *
     * @param object
     * @return
     */
    public Object filterFirst(Object object) {
        return doFilter(object, true);
    }

    private Object doFilter(Object object, boolean returnFirst) {
        checkFunctionParameter(object, "filter()");
        ScriptableList<Object> resultList = new ScriptableList<>(this.engine);
        for (Object item : this) {
            Object result = engine.callFunction((Function) object, item);
            try {
                boolean boolValue = (boolean) ConvertUtils.convert(result, Boolean.class);
                if (boolValue) {
                    if (returnFirst) {
                        return item;
                    }
                    resultList.add(item);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format("The return of the filter function must be a " +
                                "boolean value. Found: [%s] when applying on item [%s]. %s", result, item,
                        JsDevTools.decompile((Function) object)));
            }
        }
        if (returnFirst) {
            // no element found
            return null;
        } else {
            // return found elements
            return resultList;
        }
    }

    /**
     * Reduces current list to an object
     *
     * @param object
     * @return
     */
    public Object reduce(Object object) {
        checkFunctionParameter(object, "reduce()");
        Object accumulated = null;
        for (Object item : this) {
            accumulated = engine.callFunction((Function) object, accumulated, item);
        }
        return accumulated;
    }

    /**
     * Sums current elements trying to force then to floats
     *
     * @return
     */
    public float reduceSum() {
        float accumulated = 0;
        for (Object item : this) {
            accumulated += (float) ConvertUtils.convert(item, Float.class);
        }
        return accumulated;
    }

    /**
     * Counts the number of items in the list.
     *
     * @return
     */
    public int reduceCount() {
        return this.size();
    }

    private void checkFunctionParameter(Object object, String functionName) {
        if (!(object instanceof Function)) {
            throw new IllegalArgumentException(String.format("Illegal object passed callback in %s, it must be a " +
                    "function. Found: [%s]. %s.", functionName, object.getClass(), JsDevTools.decompile((Function) object)));
        }
    }

}
