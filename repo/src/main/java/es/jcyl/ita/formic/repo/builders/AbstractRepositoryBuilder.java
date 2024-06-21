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

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.repo.AbstractBaseRepository;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.repo.source.EntitySource;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

/**
 * Abstract class to support common functions for Repository builders
 */
public abstract class AbstractRepositoryBuilder<S extends EntitySource, R extends Repository> implements RepositoryBuilder<R> {

    /**
     * Factory related to current builder used to register generated instances.
     */
    private final RepositoryFactory repoFactory;

    private final Map<String, Object> properties = new HashMap<String, Object>();
    protected S source;
    protected Context context;

    public AbstractRepositoryBuilder(RepositoryFactory repoFactory) {
        this.repoFactory = repoFactory;
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
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("An error occurred trying to convert " +
                    "the value of property %s to class [%s]", property, clazz.getName()), e);
        }
        return convValue;
    }

    public void setEntitySource(S source) {
        this.source = source;
    }

    protected S getSource(){
        return this.source;
    }

    public R build() {
        R rInstance = doBuild();
        if (rInstance instanceof AbstractBaseRepository) {
            ((AbstractBaseRepository) rInstance).setId(this.source.getEntityTypeId());
        }
        // store generated instance
        repoFactory.register(this.source.getEntityTypeId(), rInstance);
        return rInstance;
    }

    /**
     * Extension point for subclass to provide particular
     * implementations of the building process.
     */
    protected abstract R doBuild();
}