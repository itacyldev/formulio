package es.jcyl.ita.formic.repo.meta.types;
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

import es.jcyl.ita.formic.repo.db.meta.GeometryType;

/**
 *
 */
public class Geometry {

    private final long srid;
    private final GeometryType geoType;
    private final String content;

    public Geometry(String content, GeometryType geoType, long srid) {
        this.content = content;
        this.srid = srid;
        this.geoType = geoType;
    }

    public long getSrid() {
        return srid;
    }

    public GeometryType getGeoType() {
        return geoType;
    }

    public String getValue() {
        return this.content;
    }

    @Override
    public String toString() {
        return String.format("[%s - %s]: %s", this.geoType.name(), this.srid, this.content );
    }
}
