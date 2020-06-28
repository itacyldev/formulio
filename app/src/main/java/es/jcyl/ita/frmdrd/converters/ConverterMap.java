package es.jcyl.ita.frmdrd.converters;

import org.mini2Dx.collections.BidiMap;
import org.mini2Dx.collections.bidimap.DualHashBidiMap;

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
 * @author Javier Ramos(javier.ramos@itacyl.es)
 */


public class ConverterMap {

    private static ConverterMap _instance;

    private static BidiMap _converters = new DualHashBidiMap();

    public static ConverterMap getInstance() {
        if (_instance == null) {
            _instance = new ConverterMap();
        }
        return _instance;
    }

    private ConverterMap() {
        registerConverter("integer", Integer.class);
        registerConverter("short", Short.class);
        registerConverter("long", Long.class);

        registerConverter("float", Float.class);
        registerConverter("double", Double.class);

        registerConverter("string", String.class);
    }

    public void registerConverter(String type, Class converterClass) {
        _converters.put(type, converterClass);
    }


    public static String getConverter(Class type) {
        return (String) _converters.getKey(type);
    }

    public static Class getConverter(String type) {
        return (Class) _converters.get(type);
    }
}
