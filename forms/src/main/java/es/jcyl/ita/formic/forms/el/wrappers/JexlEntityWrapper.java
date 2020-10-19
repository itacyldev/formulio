package es.jcyl.ita.formic.forms.el.wrappers;
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

import org.apache.commons.jexl3.JexlContext;

import es.jcyl.ita.formic.repo.Entity;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class JexlEntityWrapper implements JexlContext {
    private static final String ID_PROP = "id";

    Entity entity;

    public JexlEntityWrapper(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Object get(String name) {
        Object value = entity.get(name);
        if (value == null && ID_PROP.equalsIgnoreCase(name)) {
            value = entity.getId();
        }
        return value;
    }

    @Override
    public void set(String name, Object value) {
        entity.set(name, value);
    }

    @Override
    public boolean has(String name) {
        return entity.getProperties().containsKey(name);
    }
}
