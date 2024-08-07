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

import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteDBValue;
import es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteType;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

/**
 * Implements data type conversion from-to TEXT SQLite data type and Java objects
 */

public class SQLiteTextConverter extends SQLitePropertyConverter {


    public SQLiteTextConverter(Class javaType) {
        super(SQLiteType.TEXT, javaType);
    }

    @Override
    public SQLiteDBValue doToPersistence(Object value) {
        Object stringValue = ConvertUtils.convert(value, String.class);
        return new SQLiteDBValue(SQLiteType.TEXT, stringValue);
    }

    @Override
    public Object doToJava(SQLiteDBValue dbValue) {
        return ConvertUtils.convert(dbValue.value, this.javaType);
    }
}
