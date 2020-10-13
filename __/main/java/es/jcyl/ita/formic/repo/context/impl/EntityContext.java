package es.jcyl.ita.formic.forms.context.impl;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.context.AbstractBaseContext;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class EntityContext extends AbstractBaseContext {

    private final Entity entity;

    public EntityContext(Entity entity) {
        this.entity = entity;
        this.setPrefix("entity");
    }

    @Override
    public String getString(String key) {
        Object value = this.entity.get(key);
        return (value == null) ? "" : value.toString();
    }

    @Override
    public Object getValue(String key) {
        return this.entity.get(key);
    }

    @Override
    public int size() {
        return this.entity.getProperties().size();
    }

    @Override
    public boolean isEmpty() {
        return this.entity.getProperties().isEmpty();
    }

    @Override
    public boolean containsKey(@Nullable Object o) {
        return this.entity.getProperties().containsKey(o);
    }

    @Override
    public boolean containsValue(@Nullable Object o) {
        return this.entity.getProperties().containsValue(o);
    }

    @Nullable
    @Override
    public Object get(@NonNull Object o) {
        String propName = o.toString();
        if (propName.toLowerCase().equals("id")) {
            // direct access to entity pk as "id"
            return this.entity.getId();
        } else {
            return this.entity.get(o.toString());
        }
    }

    @Nullable
    @Override
    public Object put(String s, Object o) {
        Object prev = this.entity.get(s);
        this.entity.set(s, o);
        return prev;
    }

    @Nullable
    @Override
    public Object remove(@Nullable Object o) {
        throw new UnsupportedOperationException("Cannot remove entity properties from the context!");
    }

    @Override
    public void putAll(@NonNull Map<? extends String, ?> map) {
        for (Entry<? extends String, ?> entry : map.entrySet()) {
            this.entity.set(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Cannot remove entity properties from the context!");
    }

    @NonNull
    @Override
    public Set<String> keySet() {
        return (this.entity == null) ? new HashSet<String>() : this.entity.getProperties().keySet();
    }

    @NonNull
    @Override
    public Collection<Object> values() {
        return this.entity.getProperties().values();
    }

    @NonNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return (this.entity == null) ? new HashSet<Entry<String, Object>>() : this.entity.getProperties().entrySet();
    }
}
