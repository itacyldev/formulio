package es.jcyl.ita.formic.repo.db.builders;
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

import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.repo.builders.AbstractRepositoryBuilder;
import es.jcyl.ita.formic.repo.db.source.NativeSQLEntitySource;
import es.jcyl.ita.formic.repo.db.sqlite.RawSQLiteRepository;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class RawSQLiteRepoBuilder extends AbstractRepositoryBuilder<NativeSQLEntitySource, RawSQLiteRepository> {
    public static final String DB_ENTITY_SOURCE = "DB_ENTITY_SOURCE";

    public RawSQLiteRepoBuilder(RepositoryFactory factory) {
        super(factory);
    }

    @Override
    protected RawSQLiteRepository doBuild() {
        return new RawSQLiteRepository((NativeSQLEntitySource) this.source);
    }
}