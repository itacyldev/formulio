package es.jcyl.ita.formic.core.context.impl;
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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.jcyl.ita.formic.core.context.AbstractMapContext;
import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;

/**
 * CompositeContext implementation that allows multiple contexts related to the same key.
 * Values are inserted using LIFO strategy.
 *
 * @author ita-riobrigu
 */

public class MultiMapCompositeContext extends AbstractMapContext
        implements CompositeContext {

    private final Map<String, List<Context>> contexts = new LinkedHashMap();

    /**
     * Looks up a property in the context, looking in each of the stored context.
     * The key must contain the prefix of the parent context, ej: date.now,
     * form1.view.field.
     *
     * @param key
     * @return
     */
    public Object getValue(final String key) {
        String[] newKey = splitKey(key);
        if (newKey == null || contexts == null) {
            if (this.hasContext(key)) {
                // se accede al �ltimo contexto
                String prKey = getMapKey(key);
                List<Context> list = contexts.get(prKey);
                return list.get(list.size() - 1);
            } else {
                // intentar buscar la calve en los contextos
                Object o = getValueNested(key);
                return (o != null) ? o : super.get((Object) key);
            }
        } else {
            return (!this.hasContext(newKey[0])) ? null : getValueNested(newKey);
        }
    }

    /**
     * Recorre en orden inverso los valores de los contextos asociados al prefijo de
     * la clavel
     *
     * @param newKey
     * @return
     */
    private Object getValueNested(String[] newKey) {
        List<Context> list = contexts.get(newKey[0]);
        Context c;
        for (int i = list.size(); i-- > 0; ) {
            c = list.get(i);
            if (c.containsKey(newKey[1])) {
                return c.get(newKey[1]);
            }
        }
        return null;
    }

    private Object getValueNested(String key) {
        for (List<Context> list : contexts.values()) {
            for (int i = list.size(); i-- > 0; ) {
                Context c = list.get(i);
                if (c.containsKey(key)) {
                    return c.get(key);
                }
            }
        }
        return null;
    }

    private boolean containsKeyNested(String[] newKey) {
        List<Context> list = contexts.get(newKey[0]);
        for (Context c : list) {
            if (c.containsKey(newKey[1])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object get(String key) {
        return getValue(key);
    }

    @Override
    public Object get(Object key) {
        return getValue(key.toString());
    }

    /**
     * Checks if the key exists in the context referred by the prefix of the key.
     * Ex: contextId.key
     *
     * @param key
     * @return
     */
    public boolean containsKey(final String key) {
        String[] newKey = splitKey(key);
        if (newKey == null) {
            return super.containsKey(key);
        } else {
            return (!this.hasContext(newKey[0])) ? false : containsKeyNested(newKey);
        }

    }

    @Override
    public boolean containsKey(Object key) {
        return containsKey(key.toString());
    }

    /**
     * Composes the keys for all the items stored in the children contexts.
     *
     * @return
     */
    public Enumeration<String> getKeys() {
        final Set<String> allKeys = extractKeys();
        return Collections.enumeration(allKeys);
    }

    private Set<String> extractKeys() {
        final Set<String> allKeys = new LinkedHashSet<String>();

        for (Entry<String, List<Context>> entry : contexts.entrySet()) {
            // compose the key as contextId.property
            Set<String> contextKeys = new HashSet<String>();
            for (Context c : entry.getValue()) {
                for (String key : c.keySet()) {
                    contextKeys.add(c.getPrefix() + "." + key);
                }
            }
            allKeys.addAll(contextKeys);
        }
        allKeys.addAll(super.keySet());
        return allKeys;
    }

    @Override
    public Set<String> keySet() {
        Enumeration<String> keys = getKeys();
        return new HashSet<String>(Collections.list(keys));
    }

    @Override
    public int size() {
        Set<String> extractKeys = extractKeys();
        return (extractKeys == null) ? 0 : extractKeys.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Object put(String key, Object value) {
        String[] newKey = splitKey(key);
        if (newKey == null) { // no context prefix
            return super.put(key, value);
        } else {
            // a�adirlo en el primer contexto
            List<Context> list = contexts.get(newKey[0]);
            // El valor se a�ade en el �ltimo contexto a�adido
            Context context = list.get(list.size() - 1);
            return context.put(newKey[1], value);
        }
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public Collection<Object> values() {
        List<Object> lst = new ArrayList<Object>();
        for (Entry<String, List<Context>> entry : contexts.entrySet()) {
            List<Context> lstCtx = entry.getValue();
            for (Context c : lstCtx) {
                lst.addAll(c.values());
            }
        }
        return Collections.unmodifiableCollection(lst);
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> globalSet = new HashSet<Entry<String, Object>>();
        String fullKey;
        for (Entry<String, List<Context>> entry : contexts.entrySet()) {
            for (Context c : entry.getValue()) {
                for (Entry<String, Object> entryCtx : c.entrySet()) {
                    // add new entries with children contexts prefixes
                    fullKey = entry.getKey() + "." + entryCtx.getKey();
                    globalSet.add(new AbstractMap.SimpleEntry(fullKey, entryCtx.getValue()));
                }
            }
        }
        // add current map keys
        globalSet.addAll(super.entrySet());
        return globalSet;
    }

    private String[] splitKey(String key) {
        int lastPoint = key.lastIndexOf('.');
        if (lastPoint == -1) {
            return null;
        }
        String prefix = key.substring(0, lastPoint);
        if (!contexts.containsKey(prefix)) {
            return null;
        }
        return new String[]{prefix, key.substring(lastPoint + 1)};
    }

    /***********************************************/
    /** Composite context interface implementation */
    /***********************************************/

    /**
     * Adds child context to current composite context.
     *
     * @param context
     */
    @Override
    public void addContext(final Context context) {
        if (context instanceof CompositeContext) {
            addAllContext(((CompositeContext) context).getContexts());
        } else {
            String prKey = getMapKey(context.getPrefix());
            if (!contexts.containsKey(prKey)) {
                contexts.put(prKey, new ArrayList<Context>());

            }
            contexts.get(prKey).add(context);
        }
    }

    @Override
    public boolean hasContext(String prefix) {
        String prKey = getMapKey(prefix);
        return (getContext(prKey) != null);
    }

    public boolean hasContext(Context ctx) {
        if (ctx.getPrefix() != null) {
            return (getContext(ctx.getPrefix()) != null);
        } else {
            return contexts.containsValue(ctx);
        }
    }

    @Override
    public Context getContext(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null.");
        }
        String prKey = getMapKey(prefix);
        List<Context> list = contexts.get(prKey);
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    @Override
    public void removeContext(final Context context) {
        contexts.remove(context.getPrefix());
    }

    public void removeContext(String contextId) {
        contexts.remove(contextId);

    }

    @Override
    public void addAllContext(Collection<Context> contexts) {
        for (Context ctx : contexts) {
            addContext(ctx);
        }
    }

    /**
     * Provides collection iterator on all the stored contexts.
     *
     * @return
     */
    @Override
    public Collection<Context> getContexts() {
        List<Context> contextList = new ArrayList<Context>();
        for (List<Context> lst : contexts.values()) {
            contextList.addAll(lst);
        }
        return Collections.unmodifiableCollection(contextList);
    }

    private String getMapKey(final String prefix) {
        return prefix.toLowerCase();
    }

    @Override
    public void removeAllContexts() {
        contexts.clear();
    }

    /************************************************/
    /*** JexlContext interface implementation **/
    /************************************************/
    @Override
    public void set(String name, Object value) {
        put(name, value);
    }

    @Override
    public boolean has(String name) {
        return this.containsKey(name);
    }

}
