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

/**
 * The ValueFormatter interface provides a contract for formatting values.
 * Implementing classes must define the logic for supporting specific types
 * and formatting the values accordingly.
 *
 * @author Gustavo Río Briones
 */
public interface ValueFormatter {

    /**
     * Checks if the formatter supports formatting values of the given type.
     *
     * @param type the type of value to be formatted
     * @return true if the formatter supports the type, false otherwise
     */
    boolean supports(Class type);

    /**
     * Formats the given value according to the specified format.
     *
     * @param value  the value to be formatted
     * @param format the desired format for the value
     * @return the formatted value
     */
    Object format(Object value, String format);

}
