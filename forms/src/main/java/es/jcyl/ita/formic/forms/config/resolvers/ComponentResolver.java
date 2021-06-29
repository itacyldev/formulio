package es.jcyl.ita.formic.forms.config.resolvers;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.DevConsole;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Helper class with shortcuts to find elments in the component tree
 */
public class ComponentResolver {

    private Map<String, Set<String>> tags = new HashMap<String, Set<String>>();

    public void addComponentId(String tagName, String id) {
        tagName = tagName.toLowerCase();
        id = id.toLowerCase();
        if (!tags.containsKey(tagName)) {
            tags.put(tagName, new HashSet<String>());
        }
        if (tags.get(tagName).contains(id)) {
            throw new ConfigurationException(DevConsole.error(
                    String.format("Duplicate id for component [%s] id=[%s].", tagName, id)));
        }
        tags.get(tagName).add(id);
    }

    public void clear() {
        this.tags.clear();
    }

    public Set<String> getIdsForTag(String tagName) {
        return tags.containsKey(tagName) ? tags.get(tagName) : Collections.EMPTY_SET;
    }
}
