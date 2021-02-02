package es.jcyl.ita.formic.repo.db.spatialite.meta;
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

import android.database.Cursor;

import org.greenrobot.greendao.database.Database;

import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.db.meta.GeometryType;
import es.jcyl.ita.formic.repo.db.source.DBTableEntitySource;
import es.jcyl.ita.formic.repo.db.spatialite.converter.SpatialiteBlobConverter;
import es.jcyl.ita.formic.repo.db.sqlite.meta.SQLiteMetaModeler;
import es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteType;
import es.jcyl.ita.formic.repo.meta.types.Geometry;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

/**
 * Modifies the default mapping to assign BLOB datatype to geometry columns.
 */
public class SpatiaLiteMetaModeler extends SQLiteMetaModeler {
    private static final String SELECT_SPATIALITE_VERSION = "select spatialite_version()";

    private static final String SELECT_GEOMETRY_COLS_v3 = "select type, srid " +
            "from geometry_columns where lower(f_table_name) = ? and lower(f_geometry_column) = ?";
    private static final String SELECT_GEOMETRY_COLS_v4 = "select  geometry_type, srid " +
            "from geometry_columns where lower(f_table_name) = ? and lower(f_geometry_column) = ?";

    private static final String[] GEO_KEYS = new String[]{"polygon", "line", "point", "geo"};

    private static final SpatialiteBlobConverter sptConverter = new SpatialiteBlobConverter(Geometry.class);

    /**
     * Detects if the given type (the column data type obtained from the pragma_info
     * statement) refers to a spatialite geometry type.
     *
     * @param type
     * @return
     */
    private boolean isGeometryType(String type) {
        for (String key : GEO_KEYS) {
            if (type.toLowerCase().contains(key)) {
                return true;
            }
        }
        return false;
    }

    private String getSpatialiteVersion(Database db) {
        Cursor cursor = db.rawQuery(SELECT_SPATIALITE_VERSION, null);
        String version = "-1";
        cursor.moveToFirst();
        try {
            do {
                version = cursor.getString(0);
            } while (cursor.moveToNext());
        } finally {
            cursor.close();
        }
        return version;
    }

    public DBPropertyType createPropertyFromColumnDef(String name, String columnName, String persistenceType,
                                                      boolean isNotNull, boolean isPk,
                                                      DBTableEntitySource source) {
        // chek if type refers to a geometry column
        if (!isGeometryType(persistenceType)) {
            return super.createPropertyFromColumnDef(name, columnName, persistenceType,
                    isNotNull, isPk, source);
        } else {
            // create geometry data type
            GeometryMetadata geoMeta = readGeometryColumnInfo(source, name);
            SQLiteType dbType = SQLiteType.BLOB; // fixed storage type
            Class javaType = Geometry.class;
            SpatialitePropertyType.SpatialitePropertyTypeBuilder propBuilder =
                    new SpatialitePropertyType.SpatialitePropertyTypeBuilder(name, javaType, dbType.name(), isPk)
                            .withGeometryType(geoMeta.geoType)
                            .withSRID(geoMeta.srid);
            propBuilder.withConverter(sptConverter).withColumnName(name);
            return propBuilder.build();
        }
    }

    private GeometryMetadata readGeometryColumnInfo(DBTableEntitySource source, String columnName) {
        Database db = source.getDb();

        // read metadata from geometry_columns table
        String metadataStmt = SELECT_GEOMETRY_COLS_v4;
        String version = getSpatialiteVersion(source.getDb());
        if (version.startsWith("3")) {
            metadataStmt = SELECT_GEOMETRY_COLS_v3;
        }
        // replace tableName
        Cursor cursor = db.rawQuery(metadataStmt, new String[]{source.getTableName().toLowerCase(), columnName.toLowerCase()});
        boolean available = cursor.moveToFirst();
        int intCode;
        long srid;
        try {
            do {
                // geometry_type, srid
                intCode = cursor.getInt(0);
                srid = cursor.getLong(1);
                GeometryType geoType = GeometryType.fromIntCode(intCode);
                GeometryMetadata data = new GeometryMetadata(columnName, geoType, srid);
                // TODO: could a table have more that one geometry column?
                return data;
            } while (cursor.moveToNext());
        } finally {
            cursor.close();
        }
    }

    class GeometryMetadata {
        public String columnName;
        public GeometryType geoType;
        public long srid;

        public GeometryMetadata(String columnName, GeometryType geoType, long srid) {
            this.columnName = columnName;
            this.geoType = geoType;
            this.srid = srid;
        }
    }


}
