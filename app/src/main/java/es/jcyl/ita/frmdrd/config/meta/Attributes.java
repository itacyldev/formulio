package es.jcyl.ita.frmdrd.config.meta;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.CONVERTER;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.ID;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.LABEL;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.READONLY;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.RENDER;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.TYPE;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.VALUE;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Stores supported attibutes for each tag
 */
public class Attributes {
    private static final Map<String, Set<Attribute>> registry = new HashMap<>();

    static {
        initialize();
    }

    private static void initialize() {
        Attribute[] base = new Attribute[]{ID, VALUE, RENDER};
        Attribute[] input = new Attribute[]{TYPE, LABEL, READONLY, CONVERTER};

        Set<Attribute> baseInput = define(base, input);
        register("checkbox", baseInput);
        register("text", baseInput);
        register("date", baseInput);
    }

    private static Set<Attribute> define(Attribute[]... attributeSets) {
        Set<Attribute> atts = new HashSet();
        for (Attribute[] attSet : attributeSets) {
            atts.addAll(Arrays.asList(attSet));
        }
        return atts;

    }


    public static void register(String name, Set<Attribute> atts) {
        registry.put(name, atts);
    }

    public static Set<Attribute> getDefinition(String name) {
        return registry.get(name);
    }


}
