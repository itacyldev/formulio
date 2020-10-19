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
package es.jcyl.ita.formic.repo;

import java.util.List;

import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.query.Filter;
import es.jcyl.ita.formic.repo.source.EntitySource;

/**
 * @author Gustavo Rio (gustavo.rio@itacyl.es)
 * <p>
 * Columnar data frame repository
 */

public interface Repository<T extends Entity, F extends Filter> {
    /**
     * Repository unique identifier
     */
    String getId();

    List<T> find(F filter);

    long count(F filter);

    List<T> listAll();

    EntitySource getSource();

    EntityMeta getMeta();

    /**
     * Get the underlying instance that implements the persistence.
     *
     * @return
     */
    Object getImplementor();

    Class<F> getFilterClass();

}
