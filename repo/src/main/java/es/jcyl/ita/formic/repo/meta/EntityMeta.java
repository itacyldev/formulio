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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.repo.db.meta.KeyGeneratorStrategy;
import es.jcyl.ita.formic.repo.db.meta.MaxRowIdKeyGenerator;
import es.jcyl.ita.formic.repo.db.meta.NumericUUIDGenerator;
import es.jcyl.ita.formic.repo.db.meta.TimeStampKeyGenerator;
import es.jcyl.ita.formic.repo.db.meta.UUIDKeyGenerator;

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

    private String propertiesImplementor;

    private KeyGeneratorStrategy keyGenerator;

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
        return getPropertyByName(name) != null;
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
            if (name.toLowerCase().equals("id")) {
                // use id as alias for the id property
                return this.getIdProperties()[0];
            }
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

    public void setProperty(String propertyName, P property) {
        int i = 0;
        for (P p : properties) {
            if (p.name.equals(propertyName)) {
                properties[i] = property;
                return;
            }
            i++;
        }
        // add
        List<P> auxList = new ArrayList<P>();
        auxList.add(property);
        properties = auxList.toArray((P[]) Array.newInstance(properties.getClass(), auxList.size()));
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

    public boolean isIdProperty(String propertyName) {
        if (idProperties == null) {
            return false;
        } else {
            for (String p : idProperties) {
                if (p.equals(propertyName)) {
                    return true;
                }
            }
            return false;
        }
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

    public String getPropertiesImplementor() {
        return propertiesImplementor;
    }

    public void setPropertiesImplementor(String propertiesImplementor) {
        this.propertiesImplementor = propertiesImplementor;
    }

    public KeyGeneratorStrategy getKeyGenerator() {
        return keyGenerator;
    }

    public void setKeyGenerator(KeyGeneratorStrategy keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    public void setKeyGenerator(String type) {
        if (type.equalsIgnoreCase(KeyGeneratorStrategy.TYPE.NUMERICUUID.name())){
            setKeyGenerator(new NumericUUIDGenerator());
        }else if (type.equalsIgnoreCase(KeyGeneratorStrategy.TYPE.TIMESTAMP.name())){
            setKeyGenerator(new TimeStampKeyGenerator());
        }else if (type.equalsIgnoreCase(KeyGeneratorStrategy.TYPE.UUID.name())){
            setKeyGenerator(new UUIDKeyGenerator());
        }else{
            setKeyGenerator(new MaxRowIdKeyGenerator());
        }

    }
}
