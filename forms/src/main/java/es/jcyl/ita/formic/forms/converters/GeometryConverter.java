package es.jcyl.ita.formic.forms.converters;
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

import org.mini2Dx.beanutils.converters.AbstractConverter;

import es.jcyl.ita.formic.repo.db.meta.GeometryType;
import es.jcyl.ita.formic.repo.meta.types.ByteArray;
import es.jcyl.ita.formic.repo.meta.types.Geometry;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class GeometryConverter extends AbstractConverter {

    public GeometryConverter(Object defaultValue){
        super(defaultValue);
    }

    @Override
    protected <T> T convertToType(Class<T> targetType, Object value) throws Exception {
        if (!targetType.equals(Geometry.class)) {
            throw new UnsupportedOperationException("Not implemented yet!!");
        }

        // Convert all other types to String & handle
        String stringValue = value.toString().trim();
        if (stringValue.length() == 0) {
            return handleMissing(targetType);
        }
        // Default String conversion
        return toGeometry(targetType, stringValue);
    }

    @Override
    protected <T> T handleMissing(Class<T> type) {
        return null;
    }

    private <T> T toGeometry(Class<T> targetType, String value) {
        // TODO: this has to be improved to check srid and geo-type
        return (T) new Geometry(value, GeometryType.MULTIPOLYGON, 25830);
    }

    /**
     * ByteArray to string conversion
     *
     * @param value
     * @return
     * @throws Throwable
     */
    @Override
    protected String convertToString(Object value) throws Throwable {
        Geometry geo = (Geometry) value;
        return geo.getValue();
    }

    @Override
    protected Class<?> getDefaultType() {
        return ByteArray.class;
    }

}
