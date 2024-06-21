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

import es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteType;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class TableSQLBuilder {

    /**
     * Builds SQL for table creation using table metadata info.
     *
     * @param meta: entity persistence meta-information
     * @param ifNotExists
     * @return
     */
    public static String createTableScript(EntityMeta meta, boolean ifNotExists) {
        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        if (ifNotExists) {
            sb.append("IF NOT EXISTS ");
        }
        sb.append(meta.getName());
        sb.append(" (");
        boolean first = true;

        for (PropertyType p : meta.getProperties()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            String columnDef = createColumnDefinition(p);
            sb.append(columnDef);
        }
        sb.append(") ");
        return sb.toString();
    }

    private static String createColumnDefinition(PropertyType p) {
        SQLiteType sqLiteType = SQLiteType.getType(p.persistenceType);
        if (p.primaryKey) {
            return String.format("%s %s PRIMARY KEY", p.name, sqLiteType.toString());
        } else {
            return String.format("%s %s", p.name, sqLiteType.toString());
        }
    }

}
