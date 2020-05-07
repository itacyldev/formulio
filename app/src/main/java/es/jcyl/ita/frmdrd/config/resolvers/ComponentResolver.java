package es.jcyl.ita.frmdrd.config.resolvers;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.DevConsole;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Stores element information for current XML processing process.
 */
public class ComponentResolver {

    private Map<String, String> ids = new HashMap<String, String>();


    public void addComponentId(String id, String tagName) {
        if (ids.containsKey(id)) {
            throw new ConfigurationException(error("Duplicate id for component {tag}: " + id));
        }
        this.ids.put(id, tagName);
    }

    public void clear() {
        this.ids.clear();
    }

    public Set<String> getIdsForTag(String tagName) {
        Set<String> keys = new HashSet<String>();
        for (Map.Entry<String, String> entry : ids.entrySet()) {
            if (Objects.equals(tagName, entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }


}
