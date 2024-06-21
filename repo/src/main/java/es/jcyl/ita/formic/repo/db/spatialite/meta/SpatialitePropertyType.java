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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.db.meta.GeometryType;

/**
 * PropertyType extension to support db persistence specific features.
 */
public class SpatialitePropertyType extends DBPropertyType {

    private long srid;
    private GeometryType geometryType;

    public SpatialitePropertyType(String name, Class<?> type, String persistenceType, boolean primaryKey) {
        super(name, type, persistenceType, primaryKey);
    }

    public long getSrid() {
        return srid;
    }

    public GeometryType getGeometryType() {
        return geometryType;
    }

    @Override
    public String toString() {
        return String.format("%s (%s - %s:[%s-%s]) PRIMARY KEY: %s", this.name, this.type, this.persistenceType, this.geometryType, this.srid, primaryKey);
    }

    public static class SpatialitePropertyTypeBuilder extends DBPropertyTypeBuilder {

        public SpatialitePropertyTypeBuilder(String name, Class<?> type, String persistenceType, boolean primaryKey) {
            super(name, type, persistenceType, primaryKey);
        }

        public SpatialitePropertyTypeBuilder(SpatialitePropertyType prop) {
            super(prop);
            ((SpatialitePropertyType) property).geometryType = prop.geometryType;
            ((SpatialitePropertyType) property).srid = prop.srid;
        }

        protected DBPropertyType createInstance(String name, Class<?> type, String persistenceType, boolean primaryKey) {
            return new SpatialitePropertyType(name, type, persistenceType, primaryKey);
        }

        public SpatialitePropertyTypeBuilder withGeometryType(GeometryType geoType) {
            ((SpatialitePropertyType) property).geometryType = geoType;
            return this;
        }

        public SpatialitePropertyTypeBuilder withSRID(long srid) {
            ((SpatialitePropertyType) property).srid = srid;
            return this;
        }

        public SpatialitePropertyType build() {
            return (SpatialitePropertyType) property;
        }
    }
}
