package es.jcyl.ita.formic.repo.db.meta;
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

import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.RepositoryException;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.EntityDao;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Base class to implement entity key generation strategy
 */
public abstract class KeyGeneratorStrategy {

    public enum TYPE {MAX_ROWID, TIMESTAMP, UUID}

    private final TYPE type;

    public KeyGeneratorStrategy(TYPE type) {
        this.type = type;
    }

    public TYPE getType() {
        return this.type;
    }

    public final <T> T getKey(EntityDao dao, Entity entity, Class<T> expectedType) {
        if (!supports(expectedType)) {
            throw new RepositoryException(String.format("The generator [%s] doesn't support the pk" +
                            "type [%s]. Only [%s] are supported.", this.getClass().getName(),
                    expectedType.getName(), this.getSupportedTypes()));
        }
        return doGetKey(dao, entity, expectedType);
    }

    /**
     * Checks if current generator supports the returning key expected type
     *
     * @param type
     * @return
     */
    public abstract boolean supports(Class type);

    protected abstract <T> T doGetKey(EntityDao dao, Entity entity, Class<T> expectedType);

    protected abstract Class[] getSupportedTypes();
}
