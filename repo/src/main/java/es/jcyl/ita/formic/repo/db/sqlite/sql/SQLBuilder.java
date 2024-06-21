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

import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.db.spatialite.meta.SpatialitePropertyType;
import es.jcyl.ita.formic.repo.meta.EntityMeta;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class SQLBuilder {

    public static String createInsertInto(String insertInto, EntityMeta meta) {
        StringBuilder builder = new StringBuilder(insertInto);
        builder.append('"').append(meta.getName()).append('"').append(" (");
        appendColumns(builder, (DBPropertyType[]) meta.getProperties());
        builder.append(") VALUES (");
        appendPlaceholders(builder, (DBPropertyType[]) meta.getProperties());
        builder.append(')');
        return builder.toString();
    }


    public static StringBuilder appendColumns(StringBuilder builder, DBPropertyType[] columns) {
        int length = columns.length;

        for (int i = 0; i < length; ++i) {
            builder.append('"').append(columns[i].getColumnName()).append('"');
            if (i < length - 1) {
                builder.append(',');
            }
        }
        return builder;
    }

    public static StringBuilder appendPlaceholders(StringBuilder builder, DBPropertyType[] columns) {
        String placeHolder;
        for (int i = 0; i < columns.length; ++i) {
            // TODO: change with visitor or strategy, BDProperty and Spatialite classes are related by direct hierarchy.
            if(columns[i] instanceof SpatialitePropertyType){
                placeHolder = getPlaceHolder((SpatialitePropertyType) columns[i]);
            } else {
                placeHolder = getPlaceHolder(columns[i]);
            }
            if (i < columns.length - 1) {
                builder.append(placeHolder+",");
            } else {
                builder.append(placeHolder);
            }
        }
        return builder;
    }

    public static String getPlaceHolder(DBPropertyType property) {
        return "?";
    }

    public static String getPlaceHolder(SpatialitePropertyType propertyType) {
        return "ST_GeomFromText(?," + propertyType.getSrid() + ")";
    }

    public static String createMaxRowId(Entity entity) {
        return "select max(rowid) from " + entity.getMetadata().getName();
    }


    public static String countQuery(String query) {
        return String.format("select count(1) numResults from ( %s ) t", query);
    }
}
