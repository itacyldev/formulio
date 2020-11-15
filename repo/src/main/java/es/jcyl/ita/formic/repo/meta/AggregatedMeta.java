package es.jcyl.ita.formic.repo.meta;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Extends EntityMeta to support access to related entities' metadata
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class AggregatedMeta<P extends PropertyType> extends EntityMeta<P> {

    private Map<String, EntityMeta> mappings;
    private EntityMeta<P> delegate;

    public AggregatedMeta(String name, P[] props, String[] idProperties) {
        super(name, props, idProperties);
    }

    public AggregatedMeta(EntityMeta<P> meta) {
        super(null, null, null);
        this.delegate = meta;
    }

    public void addEntityMappings(String property, EntityMeta meta) {
        if (this.mappings == null) {
            this.mappings = new HashMap<String, EntityMeta>();
        }
        this.mappings.put(property, meta);
    }

    public boolean containsProperty(String name) {
        return delegate.containsProperty(name);
    }

    public int findPropertyByName(String name) {
        return delegate.findPropertyByName(name);
    }

    @Override
    public PropertyType getPropertyByName(String name) {
        if (!name.contains(".")) {
            return delegate.getPropertyByName(name);
        } else {
            // get a relation with the given property name
            String relPropName = name.split("\\.")[0];
            EntityMeta relMeta = this.mappings.get(relPropName);
            if (relMeta == null) {
                return null; // nof found
            } else {
                // use the rest of the property name to go deeper in the EntityMeta relations
                String restProp = name.substring(relPropName.length()+1);
                return relMeta.getPropertyByName(restProp);
            }
        }
    }

    public String getName() {
        return delegate.getName();
    }

    public void setName(String name) {
        delegate.setName(name);
    }

    public P[] getProperties() {
        return delegate.getProperties();
    }

    public String[] getPropertyNames() {
        return delegate.getPropertyNames();
    }

    public void setProperties(P[] properties) {
        delegate.setProperties(properties);
    }

    public String[] getIdPropertiesName() {
        return delegate.getIdPropertiesName();
    }

    public boolean hasIdProperties() {
        return delegate.hasIdProperties();
    }

    public boolean hasMulticolumnKey() {
        return delegate.hasMulticolumnKey();
    }

    public PropertyType[] getIdProperties() {
        return delegate.getIdProperties();
    }

    public void setIdProperties(String[] idProperties) {
        delegate.setIdProperties(idProperties);
    }

    public Integer[] getIdIndexes() {
        return delegate.getIdIndexes();
    }
}
