package es.jcyl.ita.formic.repo.db.sqlite.converter;
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

import es.jcyl.ita.formic.repo.converter.PropertyConverter;
import es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteDBValue;
import es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteType;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

/**
 * Main class to support java to SQLite data type conversion
 */
public abstract class SQLitePropertyConverter implements PropertyConverter<SQLiteType, SQLiteDBValue> {

    protected Class javaType;
    protected SQLiteType dbType;

    public SQLitePropertyConverter(SQLiteType dbType, Class javaType) {
        this.dbType = dbType;
        this.javaType = javaType;
    }



    public SQLiteType persistenceType() {
        return this.dbType;
    }

    protected SQLiteDBValue nullPersistenceValue() {
        return new SQLiteDBValue(this.dbType, null);
    }

    @Override
    public final SQLiteDBValue toPersistence(Object value) {
        if(value == null){
            return nullPersistenceValue();
        }
        return doToPersistence(value);
    }
    public abstract SQLiteDBValue doToPersistence(Object value);

    @Override
    public Class javaType() {
        return this.javaType;
    }

    @Override
    public final Object toJava(SQLiteDBValue dbValue) {
        if(dbValue.value == null){
            return nullJavaValue();
        }
        return doToJava(dbValue);
    }

    protected abstract Object doToJava(SQLiteDBValue dbValue);

    protected Object nullJavaValue() {
        return null;
    }

}
