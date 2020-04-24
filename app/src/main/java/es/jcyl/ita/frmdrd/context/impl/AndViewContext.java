package es.jcyl.ita.frmdrd.context.impl;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.jcyl.ita.crtrepo.context.AbstractBaseContext;
import es.jcyl.ita.frmdrd.view.converters.ViewValueConverter;

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
 * <p>
 * Gives access to and Android view element and its descendant values using a context interface.
 */

public class AndViewContext extends AbstractBaseContext {
    private View view;

    /**
     * Register alias to access view subelements by name
     */
    private Map<String, Alias> aliases;

    public AndViewContext(View view) {
        this.setPrefix("view");
        this.view = view;
    }

    /**
     * Registers a nested element setting its id and the converter to use to extract/set the value
     * in the view
     *
     * @param name
     * @param viewId
     */
    public void registerViewElement(String name, int viewId, ViewValueConverter converter) {
        registerViewElement(name, viewId, converter, String.class);
    }

    public void registerViewElement(String name, int viewId, ViewValueConverter converter, Class expectedType) {
        if (aliases == null) {
            this.aliases = new HashMap<>();
        }
        aliases.put(name, new Alias(name, viewId, null, converter, expectedType));
    }

    public void registerViewElement(String name, String tagName, ViewValueConverter converter, Class expectedType) {
        if (aliases == null) {
            this.aliases = new HashMap<>();
        }
        aliases.put(name, new Alias(name, -1, tagName, converter, expectedType));
    }

    /**
     * Access the component value as string, without applying the conversion using the
     * component binding expression
     *
     * @param elementId
     * @return
     */
    @Override
    public String getString(String elementId) {
        Object value = getValue(elementId);
        return (value == null) ? null : value.toString();
    }

    @Override
    public Object getValue(String name) {
        if (aliases == null) {
            return null;
        }
        Alias alias = aliases.get(name);
        if (alias == null) {
            throw new IllegalArgumentException(String.format("No view element " +
                    "registered with name [%s], call method register(name, elementId).", name));
        }
        View elementView = findElement(alias);
        // use converter to extract value from the view
        Object value = alias.converter.getValueFromView(elementView, alias.expectedType);
        return value;
    }

    private View findElement(Alias alias) {
        View elementView;
        if (alias.viewId > 0) {
            elementView = this.view.findViewById(alias.viewId);
            if (elementView == null) {
                throw new IllegalArgumentException(String.format("No view element found with id [%s] " +
                        "for alias [%s].", alias.viewId, alias.name));
            }
        } else {
            // use tagName
            elementView = this.view.findViewWithTag(alias.tagName);
            if (elementView == null) {
                throw new IllegalArgumentException(String.format("No view element found with tagName [%s] " +
                        "for alias [%s].", alias.tagName, alias.name));
            }
        }
        return elementView;
    }

    @Override
    public int size() {
        return keySet().size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(@Nullable Object o) {
        return (aliases == null) ? false : aliases.containsKey(o.toString());
    }

    @Override
    public boolean containsValue(@Nullable Object o) {
        for (Object value : this.values()) {
            if (value.equals(o)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public Object get(@Nullable Object o) {
        return getValue(o.toString());
    }

    @Nullable
    @Override
    public Object put(String name, Object value) {
        if (aliases == null) {
            return null;
        }
        Alias alias = aliases.get(name);
        if (alias == null) {
            throw new IllegalArgumentException(String.format("No view element " +
                    "registered with name [%s], call method register(name, elementId).", name));
        }
        View elementView = findElement(alias);
        alias.converter.setViewValue(elementView, value);
        return null; // don't return previous value
    }

    @Nullable
    @Override
    public Object remove(@Nullable Object o) {
        throw new UnsupportedOperationException("You can't remove one component from the view using the context!.");
    }

    @Override
    public void putAll(@NonNull Map<? extends String, ?> map) {
        throw new UnsupportedOperationException("Not supported yet!.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("You can't remove one component from the view using the context!.");
    }

    @NonNull
    @Override
    public Set<String> keySet() {
        return (aliases == null) ? Collections.EMPTY_SET : aliases.keySet();
    }

    @NonNull
    @Override
    public Collection<Object> values() {
        if (aliases == null) {
            return Collections.EMPTY_LIST;
        }
        List<Object> values = new ArrayList<>();
        for (String key : keySet()) {
            values.add(this.getValue(key));
        }
        return values;
    }

    @NonNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        if (aliases == null) {
            return Collections.EMPTY_SET;
        }
        Set<Entry<String, Object>> entries = new HashSet<>();
        for (String key : keySet()) {
            entries.add(new AbstractMap.SimpleEntry<String, Object>(key, this.getValue(key)));
        }
        return entries;
    }

    public Map<String, Alias> getAliases() {
        return aliases;
    }

    public class Alias {
        String name;
        int viewId;
        String tagName;
        ViewValueConverter converter;
        Class expectedType;

        public Alias(String name, int viewId, String tagName, ViewValueConverter converter, Class expectedType) {
            this.name = name;
            this.viewId = viewId;
            this.tagName = tagName;
            this.converter = converter;
            this.expectedType = expectedType;
        }

        @NonNull
        @Override
        public String toString() {
            return String.format("name: %s  viewId: %s - tagName: %s",name, viewId, tagName);
        }
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}

