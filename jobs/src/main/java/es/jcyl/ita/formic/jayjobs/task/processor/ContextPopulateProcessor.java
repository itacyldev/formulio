package es.jcyl.ita.formic.jayjobs.task.processor;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * Processor that allows to set values in context
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class ContextPopulateProcessor extends AbstractProcessor
        implements NonIterProcessor {

    private Object value;
    private String name = "value";
    private Map<String, Object> valueMap;

    @Override
    public void process() {
        if (StringUtils.isNotBlank(this.name)) {
            this.getTaskContext().put(this.name, this.value);
        }
        if (this.valueMap != null) {
            this.getTaskContext().putAll(this.valueMap);
        }
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getValueMap() {
        return valueMap;
    }

    public void setValueMap(Map<String, Object> valueMap) {
        this.valueMap = valueMap;
    }
}
