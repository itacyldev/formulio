package es.jcyl.ita.formic.repo.query;
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
import es.jcyl.ita.formic.repo.RepositoryException;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class FilterRepoUtils {

    public static Filter createInstance(Repository repo) {
        Filter f;
        try {
            f = (Filter) repo.getFilterClass().newInstance();
        } catch (Exception e) {
            throw new RepositoryException("An error occurred while trying to instantiate the filter " +
                    "class: " + repo.getFilterClass().getName());
        }
        return f;
    }

    public static Filter clone(Repository repo, Filter filter){
        Filter f = createInstance(repo);
        if (filter != null) {
            f.setExpression(filter.getExpression());
            f.setSorting(filter.getSorting());
        }
        return f;
    }
}
