package es.jcyl.ita.formic.repo.builders;
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

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.repo.source.EntitySource;
import es.jcyl.ita.formic.repo.source.EntitySourceFactory;
import es.jcyl.ita.formic.repo.source.Source;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

/**
 * Abstract class to support common functions for Repository builders
 */
public abstract class AbstractEntitySourceBuilder<ES extends EntitySource> implements EntitySourceBuilder<ES> {

    public static final String SOURCE = "SOURCE";
    public static final String ENTITY_TYPE_ID = "ENTITY_TYPE_ID";

    /**
     * Factory related to current builder used to register generated instances.
     */
    private final EntitySourceFactory sourceFactory;
    private final Map<String, Object> properties = new HashMap<String, Object>();

    public AbstractEntitySourceBuilder(EntitySourceFactory sourceFactory) {
        this.sourceFactory = sourceFactory;
    }

    public void withProperty(String property, Object value) {
        this.properties.put(property, value);
    }

    public <T> T getProperty(String property, Class<T> clazz) {
        T convValue = null;
        Object value = this.properties.get(property);
        if (value == null) {
            return null;
        }
        try {
            convValue = (T) value;
        } catch (Throwable e) {
            throw new IllegalArgumentException(String.format("An error occurred trying to convert " +
                    "the value of property %s to class [%s]", property, clazz.getName()), e);
        }
        return convValue;
    }

    protected Source getSource() {
        return getProperty(SOURCE, Source.class);
    }

    protected String getEntityTypeId() {
        return getProperty(ENTITY_TYPE_ID, String.class);
    }

    public ES build() {
        // check source
        if (this.getSource() == null) {
            throw new IllegalStateException("No source object provided, set the property SOURCE to " +
                    "the builder before trying to get and EntitySource instance.");
        }
        if (StringUtils.isBlank(this.getEntityTypeId())) {
            throw new IllegalStateException("No entityTypeId provided, set the property ENTITY_TYPE_ID " +
                    "before trying to get and EntitySource instance.");
        }
        ES instance = doBuild();
        // store generated instance
        sourceFactory.register(instance);
        return instance;
    }

    /**
     * Extension point for subclass to provide particular implementations of the building process.
     */
    protected abstract ES doBuild();
}