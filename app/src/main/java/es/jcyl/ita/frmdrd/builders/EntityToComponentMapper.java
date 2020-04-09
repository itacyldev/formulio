package es.jcyl.ita.frmdrd.builders;
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
import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.crtrepo.types.ByteArray;
import es.jcyl.ita.frmdrd.ui.components.UIField;


/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Default mapping to obtain the default UIField assignable for each java class
 */
public class EntityToComponentMapper {

    private Map<Class, UIField.TYPE> defaultMap;

    public EntityToComponentMapper() {
        defaultMap = new HashMap<>();
        defaultMap.put(Boolean.class, UIField.TYPE.BOOLEAN);
        defaultMap.put(Date.class, UIField.TYPE.DATE);
        defaultMap.put(Character.class, UIField.TYPE.TEXT);
        defaultMap.put(String.class, UIField.TYPE.TEXT);
        defaultMap.put(Short.class, UIField.TYPE.TEXT);
        defaultMap.put(Integer.class, UIField.TYPE.TEXT);
        defaultMap.put(Long.class, UIField.TYPE.TEXT);
        defaultMap.put(Float.class, UIField.TYPE.TEXT);
        defaultMap.put(Double.class, UIField.TYPE.TEXT);
        defaultMap.put(ByteArray.class, UIField.TYPE.TEXT);

    }

    public UIField.TYPE getComponent(Class clazz) {
        if (!defaultMap.containsKey(clazz)) {
            throw new IllegalArgumentException(
                    String.format("No default UIField mapping for class type [%s].", clazz.getName()));
        }
        return defaultMap.get(clazz);
    }
}
