package es.jcyl.ita.formic.repo.meta;
/*
 * Copyright 2011-2020 the original author or authors.
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

import org.apache.commons.lang3.ArrayUtils;

/**
 * Describes the information needed to persist the current type of entity.
 */
public class EntityMeta<P extends PropertyType> {

    /**
     * unique entity name
     */
    private String name;
    /**
     * Entity properties
     */
    private P[] properties;
    /**
     * Property set that identifies the entity uniquely
     */
    private String[] idProperties;
    /**
     * Position of the pk properties in the array of properties
     */
    private Integer[] idIndexes;

    public EntityMeta(String name, P[] props, String[] idProperties) {
        this.name = name;
        this.properties = props;
        this.idProperties = idProperties;
        if (idProperties != null) {
            this.idIndexes = findIdIndexes();
        }
    }

    /**
     * Returns true if the property exists in the metadata
     *
     * @param name
     * @return
     */
    public boolean containsProperty(String name) {
        if ("id".equals(name) && ArrayUtils.isNotEmpty(idProperties)) {
            return true; // it has a primaryKey
        }
        int pos = findPropertyByName(name);
        return pos >= 0;
    }

    public int findPropertyByName(String name) {
        int pos = 0;
        for (PropertyType p : this.properties) {
            if (p.getName().equalsIgnoreCase(name)) {
                return pos;
            }
            pos++;
        }
        return -1;
    }

    public PropertyType getPropertyByName(String name) {
        int pos = findPropertyByName(name);
        if (pos == -1) {
            return null;
        } else {
            return this.properties[pos];
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public P[] getProperties() {
        return properties;
    }

    public String[] getPropertyNames() {
        String[] names = new String[this.properties.length];
        for (int i = 0; i < this.properties.length; i++) {
            names[i] = this.properties[i].getName();
        }
        return names;
    }

    public void setProperties(P[] properties) {
        this.properties = properties;
    }

    public String[] getIdPropertiesName() {
        return idProperties;
    }

    public boolean hasIdProperties() {
        return (this.idProperties != null && this.idProperties.length > 0);
    }
    public boolean hasMulticolumnKey() {
        return (this.idProperties != null && this.idProperties.length > 1);
    }

    public PropertyType[] getIdProperties() {
        PropertyType[] pk = new PropertyType[this.idIndexes.length];
        for (int i = 0; i < pk.length; i++) {
            pk[i] = properties[idIndexes[i]];
        }
        return pk;
    }

    public void setIdProperties(String[] idProperties) {
        this.idProperties = idProperties;
        if (idProperties != null) {
            this.idIndexes = findIdIndexes();
        } else {
            this.idIndexes = new Integer[0];
        }
    }

    public Integer[] getIdIndexes() {
        return idIndexes;
    }


    private Integer[] findIdIndexes() {
        Integer[] idxs = new Integer[this.idProperties.length];
        int i = 0;
        for (String idName : idProperties) {
            // find position in
            int pos = 0;
            for (P p : properties) {
                if (p.getName().equals(idName)) {
                    idxs[i] = pos;
                    break;
                }
                pos++;
            }
            i++;
        }
        return idxs;
    }


}
