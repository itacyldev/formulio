package es.jcyl.ita.formic.core.format;
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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The DateFormatter class is an implementation of the ValueFormatter interface
 * that converts a Date object to a String representation based on the specified format.
 *
 * @author Gustavo Río Briones
 */
public class DateValueFormatter implements ValueFormatter {

    /**
     * Checks if the formatter supports formatting values of the given type (Date).
     *
     * @param type the type of value to be formatted
     * @return true if the formatter supports the type (Date), false otherwise
     */
    @Override
    public boolean supports(Class type) {
        return type == Date.class;
    }

    /**
     * Formats the given Date value according to the specified format.
     *
     * @param value  the Date value to be formatted
     * @param format the desired format for the Date value
     * @return the formatted String representation of the Date value
     */
    @Override
    public Object format(Object value, String format) {
        if (value instanceof Date) {
            Date date = (Date) value;
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(date);
        }
        throw new IllegalArgumentException(
                String.format("Value type not supported: %s", value.getClass().getName()));
    }

}
