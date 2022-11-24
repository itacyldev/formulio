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

import android.renderscript.Script;

import org.apache.commons.beanutils.Converter;
import org.mini2Dx.beanutils.ConvertUtils;
import org.mozilla.javascript.Function;

import java.util.ArrayList;

import es.jcyl.ita.formic.repo.converter.ConverterUtils;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class ScriptableList<T> extends ArrayList<T> {
    private final ScriptEngine engine;

    public ScriptableList(ScriptEngine engine) {
        this.engine = engine;
    }

    /**
     * Applies the given function to all the elements of current list
     *
     * @param object
     */
    public ScriptableList<Object> apply(Object object) {
        if (!(object instanceof Function)) {
            throw new IllegalArgumentException(String.format("Illegal object passed to function apply(), it must be a " +
                    "function. Found: [%s]. %s.", object.getClass(), object));
        }
        ScriptableList<Object> resultList = new ScriptableList<>(this.engine);
        for (Object item : this) {
            Object result = engine.callFunction((Function) object, item);
            resultList.add(result);
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
        if (!(object instanceof Function)) {
            throw new IllegalArgumentException(String.format("Illegal object passed to function apply(), it must be a " +
                    "function. Found: [%s]. %s.", object.getClass(), object));
        }
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
                throw new IllegalArgumentException(String.format("The return of the filter function must be a boolean value." +
                        "Found: [%s] when applying on item [%s].", result, item));
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

}
