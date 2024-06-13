package es.jcyl.ita.formic.repo.db.meta;
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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public enum GeometryType {
    GEOMETRY(0, false, null, null),
    POINT(1, false, "MULTIPOINT", "castToPoint"),
    LINESTRING(2, false, "MULTILINESTRING", "castToLineString"),
    POLYGON(3, false, "MULTIPOLYGON", "castToPolygon"),
    MULTIPOINT(4, true, "MULTIPOINT", "castToMultiPoint"),
    MULTILINESTRING(5, true, "MULTILINESTRING", "castToMultiLineString"),
    MULTIPOLYGON(6, true, "MULTIPOLYGON", "castToMultiPolygon"),
    GEOMETRYCOLLECTION(7, false, null, "castToGeometryCollection");

    private int value;
    private boolean multi;
    private String multi_type_associate;
    private String cast_function;

    GeometryType(int value, boolean multi, String multi_type_associate, String cast_function) {
        this.value = value;
        this.multi = multi;
        this.multi_type_associate = multi_type_associate;
        this.cast_function = cast_function;
    }

    public static GeometryType fromIntCode(int code) {
        int roundedCode = code - code / 1000;
        for (GeometryType geometryType : GeometryType.values()) {
            if (geometryType.getValue() == roundedCode) {
                return geometryType;
            }
        }
        throw new RuntimeException("Illegal GeometryType code: " + code);
    }

    public int getValue() {
        return value;
    }

    public boolean isMulti() {
        return multi;
    }

    public String getMultiTypeAssociate() {
        return multi_type_associate;
    }

    public String getCastFunction() {
        return cast_function;
    }
}