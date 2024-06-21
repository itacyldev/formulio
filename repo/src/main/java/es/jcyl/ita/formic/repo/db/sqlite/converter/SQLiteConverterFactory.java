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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.repo.RepositoryException;
import es.jcyl.ita.formic.repo.converter.ConverterFactory;
import es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteType;
import es.jcyl.ita.formic.repo.meta.types.ByteArray;
import es.jcyl.ita.formic.repo.meta.types.Geometry;

import static es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteType.BLOB;
import static es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteType.INTEGER;
import static es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteType.NULL;
import static es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteType.REAL;
import static es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteType.TEXT;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class SQLiteConverterFactory implements ConverterFactory<SQLitePropertyConverter, SQLiteType> {
    private static SQLiteConverterFactory _instance = null;


    private Map<String, SQLitePropertyConverter> converters;
    private Map<Class, SQLitePropertyConverter> defaultConverters;
    private HashMap<SQLiteType, SQLitePropertyConverter> defaultDBConverters;

    public static SQLiteConverterFactory getInstance() {
        if (_instance == null) {
            _instance = new SQLiteConverterFactory();
        }
        return _instance;
    }

    private SQLiteConverterFactory() {
        // init converters
        converters = new HashMap<String, SQLitePropertyConverter>();
        initDefaultConverters();
    }

    @Override
    public SQLitePropertyConverter getConverter(Class javaType, SQLiteType dbType) {
        String key = String.format("%s-%s", javaType.getCanonicalName(), dbType.name());

        if (!this.converters.containsKey(key)) {
            SQLitePropertyConverter converter = defaultDBConverters.get(dbType);
            try {
                converter = (SQLitePropertyConverter) converter
                        .getClass().getDeclaredConstructor(new Class[]{Class.class}).newInstance(javaType);
            } catch (Exception e) {
                throw new RepositoryException(String.format("Can't create converter from " +
                        "javaType: %s to dbType [%s].", javaType, dbType.name(), e));
            }
            this.converters.put(key, converter);
        }
        return converters.get(key);
    }

    @Override
    public SQLitePropertyConverter getDefaultConverter(Class javaType) {
        SQLitePropertyConverter obj = this.defaultConverters.get(javaType);
        if (obj == null) {
            throw new RepositoryException(String.format("No default converter found for java Class [%s].", javaType.getName()));
        }
        return obj;
    }

    @Override
    public SQLitePropertyConverter getDefaultConverter(SQLiteType dbType) {
        SQLitePropertyConverter obj = this.defaultDBConverters.get(dbType);
        if (obj == null) {
            throw new RepositoryException(String.format("No default converter found for SQLite type [%s].", dbType.toString()));
        }
        return obj;
    }

    public void register(String converterId, SQLitePropertyConverter converter) {
        this.converters.put(converterId, converter);
    }

    private void initDefaultConverters() {
        this.defaultConverters = new HashMap<>();
        //TODO. subclass converters to get more robust java to db transformations
        this.defaultConverters.put(String.class, new SQLiteTextConverter(String.class));
        this.defaultConverters.put(Integer.class, new SQLiteIntegerConverter(Integer.class));
        this.defaultConverters.put(Long.class, new SQLiteIntegerConverter(Long.class));
        this.defaultConverters.put(Float.class, new SQLiteRealConverter(Float.class));
        this.defaultConverters.put(Double.class, new SQLiteRealConverter(Double.class));
        this.defaultConverters.put(Boolean.class, new SQLiteIntegerConverter(Boolean.class));
        this.defaultConverters.put(Date.class, new SQLiteIntegerConverter(Date.class));
        this.defaultConverters.put(ByteArray.class, new SQLiteBlobConverter(Date.class));
        this.defaultConverters.put(Geometry.class, new SQLiteBlobConverter(Date.class));


        // default converters to use when metadata is read from the table directly
        this.defaultDBConverters = new HashMap<>();
        this.defaultDBConverters.put(NULL, new SQLiteTextConverter(String.class));
        this.defaultDBConverters.put(TEXT, new SQLiteTextConverter(String.class));
        this.defaultDBConverters.put(INTEGER, new SQLiteIntegerConverter(Long.class));
        this.defaultDBConverters.put(REAL, new SQLiteRealConverter(Double.class));
        this.defaultDBConverters.put(BLOB, new SQLiteBlobConverter(ByteArray.class));
    }

}
