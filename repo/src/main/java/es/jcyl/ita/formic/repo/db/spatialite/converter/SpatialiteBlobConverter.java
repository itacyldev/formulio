package es.jcyl.ita.formic.repo.db.spatialite.converter;
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

import es.jcyl.ita.formic.repo.RepositoryException;
import es.jcyl.ita.formic.repo.db.meta.GeometryType;
import es.jcyl.ita.formic.repo.db.sqlite.converter.SQLitePropertyConverter;
import es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteDBValue;
import es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteType;
import es.jcyl.ita.formic.repo.meta.types.Geometry;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

/**
 * Implements data type conversion from-to BLOB SQLite data type and Java objects
 */

public class SpatialiteBlobConverter extends SQLitePropertyConverter {


    public SpatialiteBlobConverter(Class javaType) {
        super(SQLiteType.BLOB, javaType);
    }

    @Override
    public SQLiteDBValue doToPersistence(Object value) {
        if (!(value instanceof Geometry)) {
            throw new RepositoryException(String.format("Object type not supported: [%s].", value.getClass().getName()));
        }
        return new SQLiteDBValue(this.dbType, value);
    }

    /**
     * Converts to different Java types from SQLite INTEGER data type
     *
     * @param dbValue
     * @return
     */
    @Override
    public Object doToJava(SQLiteDBValue dbValue) {
        // BLOB values are read as byte[]
        byte[] bValue = (byte[]) dbValue.value;
        return new Geometry(new String(bValue), GeometryType.POLYGON, 25830);
    }
}
