package es.jcyl.ita.formic.repo;
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


import es.jcyl.ita.formic.repo.query.Filter;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public interface EditableRepository<T extends Entity, ID, F extends Filter> extends Repository<T, F> {

    T findById(ID id);

    boolean existsById(ID id);

    void save(T entity);

    void delete(T entity);

    void deleteById(ID id);

    void deleteAll();

    /**
     * Creates an empty entity with meta and source parameters set, but won't be persisted until
     * save() method is call on it.
     * @return
     */
    T newEntity();
}
