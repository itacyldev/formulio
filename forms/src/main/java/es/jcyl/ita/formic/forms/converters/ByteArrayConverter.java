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

import java.util.Date;

import es.jcyl.ita.formic.repo.meta.types.ByteArray;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ByteArrayConverter extends AbstractConverter {

    @Override
    protected <T> T convertToType(Class<T> targetType, Object value) throws Exception {
        if (!targetType.equals(ByteArray.class)) {
            throw new UnsupportedOperationException("Not implemented yet!!");
        }
        // Handle Long
        if (value instanceof Long) {
            return toByteArray(targetType, (Long) value);
        }
        if (value instanceof Integer) {
            Long lValue = ((Integer) value).longValue();
            return toByteArray(targetType, lValue);
        }
        // Handle Date
        if (value instanceof Date) {
            return toByteArray(targetType, ((Date) value).getTime());
        }

        // Convert all other types to String & handle
        String stringValue = value.toString().trim();
        if (stringValue.length() == 0) {
            return handleMissing(targetType);
        }
        // Default String conversion
        return toByteArray(targetType, stringValue);
    }

    private <T> T toByteArray(Class<T> targetType, String value) {
        return (T) new ByteArray(value.getBytes());
    }

    private <T> T toByteArray(Class<T> targetType, Long value) {
        // good enough
        return (T) new ByteArray(String.valueOf(value).getBytes());
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
        ByteArray ba = (ByteArray) value;
        return new String(ba.getValue());
    }

    @Override
    protected Class<?> getDefaultType() {
        return ByteArray.class;
    }

}
