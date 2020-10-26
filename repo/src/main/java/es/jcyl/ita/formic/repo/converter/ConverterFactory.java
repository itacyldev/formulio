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

public interface ConverterFactory<C, D> {

    /**
     * Returns a converter suitable for conversion from type D to the given javaType.
     * @return
     */
    C getConverter(Class javaType, D dbType);

    /**
     * Returns the default converter for a given java Class.
     * @param javaType
     * @return
     */
    C getDefaultConverter(Class javaType);


    /**
     * Returns de default converter for the given db data type.
     * @param dbType
     * @return
     */
    C getDefaultConverter(D dbType);

    /**
     * Registers a converter instance in the factory.
     * @param converterId
     * @param converter
     */
    void register(String converterId, C converter);

}
