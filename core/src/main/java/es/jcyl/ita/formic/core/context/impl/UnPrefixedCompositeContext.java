package es.jcyl.ita.formic.core.context.impl;

import org.apache.commons.jexl3.JexlContext;

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

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;

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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UnPrefixedCompositeContext extends MapCompositeContext implements CompositeContext, JexlContext {
    private final Map<String, Context> contexts = new LinkedHashMap();

    /**
     * Looks up a property in the context, looking in each of the stored context. The key must
     * contain the prefix of the parent context, ej: date.now, form1.view.field.
     *
     * @param key
     * @return
     */
    public Object getValue(final String key) {
        String[] newKey = splitKey(key);
        return (newKey == null || this.contexts == null) ? super.get(key) :
                this.contexts.get(newKey[0]).get(newKey[1]);
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
     * Checks if the key exists in the context referred by the prefix of the key. Ex: contextId.key
     *
     * @param key
     * @return
     */
    public boolean containsKey(final String key) {
        String[] newKey = splitKey(key);
        if (newKey == null) {
            return super.containsKey(key);
        } else {
            return (!this.hasContext(newKey[0])) ? false : this.contexts.get(newKey[0]).containsKey(newKey[1]);
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
        final Set<String> allKeys = new LinkedHashSet<String>();

        for (Entry<String, Context> entry : this.contexts.entrySet()) {
            // compose the key as contextId.property
            Set<String> contextKeys = new HashSet<String>();
            for (String s : entry.getValue().keySet()) {
                contextKeys.add(entry.getKey() + "." + s);
            }
            allKeys.addAll(contextKeys);
        }
        allKeys.addAll(super.keySet());
        return Collections.enumeration(allKeys);
    }

    @Override
    public Set<String> keySet() {
        Enumeration<String> keys = getKeys();
        return new HashSet<String>(Collections.list(keys));
    }

    @Override
    public int size() {
        int size = 0;
        for (Entry<String, Context> entry : this.contexts.entrySet()) {
            size += entry.getValue().size();
        }
        return size;
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
            Context context = this.contexts.get(newKey[0]);
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
        for (Entry<String, Context> entry : this.contexts.entrySet()) {
            lst.addAll(entry.getValue().values());
        }
        return Collections.unmodifiableCollection(lst);
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> globalSet = new HashSet<Entry<String, Object>>();
        String fullKey;
        for (Entry<String, Context> entry : this.contexts.entrySet()) {

            for (Entry<String, Object> entryCtx : entry.getValue()
                    .entrySet()) {
                // add new entries with children contexts prefixes
                fullKey = entry.getKey() + "." + entryCtx.getKey();
                globalSet.add(new AbstractMap.SimpleEntry(fullKey,
                        entryCtx.getValue()));
            }
        }
        // add current map keys
        globalSet.addAll(super.entrySet());
        return globalSet;
    }

    private String[] splitKey(String key) {
        int firstPointPos = key.indexOf('.');
        if (firstPointPos == -1) {
            return null;
        }
        String prefix = key.substring(0, firstPointPos);
        if (!this.contexts.containsKey(prefix)) {
//            throw new ContextException(String.format("No context found with the prefix [%s].", prefix));
        }
        return new String[]{prefix, key.substring(firstPointPos + 1)};
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
            this.contexts.put(prKey, context);
        }
    }

    @Override
    public boolean hasContext(String prefix) {
        return (this.getContext(prefix) != null);
    }

    public boolean hasContext(Context ctx) {
        if (ctx.getPrefix() != null) {
            return (this.getContext(ctx.getPrefix()) != null);
        } else {
            return this.contexts.containsValue(ctx);
        }
    }

    @Override
    public Context getContext(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null.");
        }
        return this.contexts.get(getMapKey(prefix));
    }

    @Override
    public void removeContext(final Context context) {
        this.contexts.remove(context.getPrefix());
    }

    @Override
    public void removeContext(String contextId) {
        this.contexts.remove(contextId);

    }

    @Override
    public void addAllContext(Collection<Context> contexts) {
        for (Context ctx : contexts) {
            this.addContext(ctx);
        }
    }


    /**
     * Provides collection iterator on all the stored contexts.
     *
     * @return
     */
    @Override
    public Collection<Context> getContexts() {
        return Collections.unmodifiableCollection(this.contexts.values());
    }

    private String getMapKey(final String prefix) {
        return prefix.toLowerCase();
    }

    @Override
    public void removeAllContexts() {
        this.contexts.clear();
    }

    /************************************************/
    /*** JexlContext interface implementation **/
    /************************************************/
    public void set(String name, Object value) {
        this.put(name, value);
    }

    public boolean has(String name) {
        return this.containsKey(name);
    }

}
