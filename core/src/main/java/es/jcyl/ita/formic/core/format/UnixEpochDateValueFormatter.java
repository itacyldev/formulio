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

import java.util.Date;

public class UnixEpochDateValueFormatter implements ValueFormatter {

    private static final String FORMAT_UNIXEPOCH_S = "seconds";
    private static final String FORMAT_UNIXEPOCH_MS = "milliseconds";

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
     * Formats the given Date value to Unix Epoch format based on the specified format.
     *
     * @param value  the Date value to be formatted
     * @param format the desired format ("unixepoch_s" for Unix Epoch in seconds,
     *               "unixepoch_ms" for Unix Epoch in milliseconds)
     * @return the formatted Unix Epoch value as a long (seconds or milliseconds),
     *         or null if the value is not of type Date or the format is not supported
     */
    @Override
    public Object format(Object value, String format) {
        if (value instanceof Date) {
            Date date = (Date) value;
            if (FORMAT_UNIXEPOCH_S.equals(format)) {
                return date.getTime() / 1000; // Unix Epoch in seconds
            } else if (FORMAT_UNIXEPOCH_MS.equals(format)) {
                return date.getTime(); // Unix Epoch in milliseconds
            }
        }
        return null;
    }

}
