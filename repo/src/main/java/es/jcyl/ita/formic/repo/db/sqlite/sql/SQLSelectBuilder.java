package es.jcyl.ita.formic.repo.db.sqlite.sql;
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

import es.jcyl.ita.formic.repo.db.SQLQueryFilter;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class SQLSelectBuilder {

    public static String paginate(String query, SQLQueryFilter filter) {
        // TODO: preliminary version
        StringBuffer stb = new StringBuffer(query);
        if (filter == null) {
            return stb.toString();
        }
        if (filter.getPageSize() != -1) {
            stb.append(" LIMIT " + filter.getPageSize() + " ");
        }
        if (filter.getOffset() > 0) {
            stb.append(" OFFSET " + filter.getOffset());
        }
        return stb.toString();
    }
}
