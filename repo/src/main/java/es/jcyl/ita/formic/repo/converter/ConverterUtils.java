package es.jcyl.ita.formic.repo.converter;
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

public class ConverterUtils {

    /**
     * Securely
     *
     * @return
     */
    public static Long convertNumericToLong(Object value) {
        if (value instanceof Integer) {
            return Long.valueOf((Integer) value);
        } else if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Short) {
            return Long.valueOf((Short) value);
        } else if (value instanceof Byte) {
            return Long.valueOf((Byte) value);
        } else {
            throw new RuntimeException("Not numeric type: " + value.getClass());
        }

    }
}
