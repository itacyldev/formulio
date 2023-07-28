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
import java.util.HashMap;
import java.util.Map;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class ValueFormatterFactory {

    private static final ValueFormatterFactory INSTANCE = new ValueFormatterFactory();
    private Map<String, ValueFormatter> formatterMap;

    private ValueFormatterFactory() {
        formatterMap = new HashMap<>();
        ValueFormatter uxeFormatter = new UnixEpochDateValueFormatter();
        formatterMap.put("seconds", uxeFormatter);
        formatterMap.put("milliseconds", uxeFormatter);
        formatterMap.put("date:default", new DateValueFormatter());
    }

    public static ValueFormatterFactory getInstance() {
        return INSTANCE;
    }

    public ValueFormatter getFormatter(Class valueType, String format) {
        return getFormatter(valueType, format, null);
    }
    public ValueFormatter getFormatter(Class valueType, String format, Class returningType) {
        ValueFormatter formatter = null;
        if (valueType == Date.class) {
            if (formatterMap.containsKey(format)) {
                formatter = formatterMap.get(format);
            } else {
                formatter = formatterMap.get("date:default");
            }
        }
        if (formatter == null) {
            throw new IllegalArgumentException(String.format("No formatter found for property type %s and format %s. ",
                    "support data type %s", valueType.getSimpleName(), format));
        }

        if (!formatter.supports(valueType)) {
            throw new IllegalArgumentException(String.format("Invalid formatter, this formatter %s doesn't " +
                    "support data type %s", formatter.getClass().getSimpleName(), valueType.getSimpleName()));
        }
        return formatter;
    }
}
