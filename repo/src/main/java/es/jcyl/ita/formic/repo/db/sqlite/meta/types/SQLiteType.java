package es.jcyl.ita.formic.repo.db.sqlite.meta.types;
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

import org.apache.commons.lang3.StringUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public enum SQLiteType {
    TEXT, INTEGER, REAL, BLOB, NULL;

    private static final CharSequence CHAR = "CHAR";
    private static final CharSequence CLOB = "CLOB";
    private static final CharSequence INT = "INT";
    private static final CharSequence FLOA = "FLOA";
    private static final CharSequence DOUB = "DOUB";
    /**
     * Equivalente definitions for native data types.
     */
    private String[] affinityTypes;
    private static String[] integerAffinityTypes = new String[]{"INT", "TINYINT", "SMALLINT", "MEDIUMINT",
            "BIGINT", "UNSIGNED BIG INT", "INT2", "INT8",};

    public static SQLiteType getType(int type) {
        switch (type) {
            case 0:
                return NULL;
            case 1:
                return INTEGER;
            case 2:
                return REAL;
            case 3:
                return TEXT;
            case 4:
                return BLOB;
        }
        throw new IllegalArgumentException(String.format("DataType not supported: [%s]", type));
    }

    /**
     * Return the SQLite storing data type following the affinity rules as defined
     * in https://www.sqlite.org/datatype3.html
     *
     * @param typeName
     * @return
     */
    public static SQLiteType getType(String typeName) {
        for (SQLiteType v : values()) {
            if (v.toString().equalsIgnoreCase(typeName)) return v;
        }
        // try affinity Rules as defined in https://www.sqlite.org/datatype3.html
        //1. If the declared type contains the string "INT" then it is assigned INTEGER affinity.
        if (typeName.contains(INT)) {
            return INTEGER;
        }
        //If the declared type of the column contains any of the strings "CHAR", "CLOB", or "TEXT"
        // then that column has TEXT affinity. Notice that the type VARCHAR contains the string
        // "CHAR" and is thus assigned TEXT affinity.
        if (typeName.contains(CHAR) || typeName.contains(CLOB)) {
            return TEXT;
        }
        //If the declared type for a column contains the string "BLOB" or if no type is specified
        // then the column has affinity BLOB.
        if (StringUtils.isBlank(typeName)) {
            return BLOB;
        }
        //If the declared type for a column contains any of the strings "REAL", "FLOA", or "DOUB"
        // then the column has REAL affinity.
        if (typeName.contains(FLOA) || typeName.contains(DOUB)) {
            return REAL;
        }
        return INTEGER;
    }
}
