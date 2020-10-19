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

public interface PropertyConverter<T, V> {

    /**
     * Defines data type used by persistence layer to store the Java Objects
     * @return
     */
    T persistenceType();

    /**
     * Converts the Object into the proper persistence representation
     * @param value
     * @return
     */
    V toPersistence(Object value);

    /**
     * Returns the java data type used by this converter to create java representations for the
     * current persistence data type.
     * @return
     */
    Class javaType();

    /**
     * Converts provided persistence value (V) to a java Object.
     * @param value
     * @return
     */
    Object toJava(V value);

}
