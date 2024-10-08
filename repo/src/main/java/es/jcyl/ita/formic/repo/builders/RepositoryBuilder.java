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

import es.jcyl.ita.formic.repo.Repository;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public interface RepositoryBuilder<R extends Repository> {

    /**
     * Se properties to configure the repository building process.
     *
     * @param property
     * @param value
     */
    void withProperty(String property, Object value);

    /**
     * Creates the repository and registers it in the system as a reusable instance.
     *
     * @return
     */
    R build();
}
