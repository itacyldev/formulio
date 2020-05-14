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

import java.util.HashMap;
import java.util.Map;

import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.CONVERTER;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.DBFILE;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.DBTABLE;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.DESCRIPTION;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.ENTITYSELECTOR;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.FILTERING;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.HEADER_TEXT;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.ID;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.INPUT_TYPE;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.LABEL;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.MAINFORM;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.NAME;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.ONSAVE;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.ORDERING;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.PROPERTIES;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.READONLY;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.RENDER;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.REPO;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.ROUTE;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.TYPE;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.VALUE;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Stores supported attibutes for each tag
 */
public class TagDef {
    private static final Map<String, Map<String, Attribute>> registry = new HashMap<>();

    static {
        initialize();
    }

    private static void initialize() {

        Attribute[] baseRepoAccessor = new Attribute[]{ID, PROPERTIES, REPO, DBFILE, DBTABLE};
        register("main", define(baseRepoAccessor, new Attribute[]{NAME, DESCRIPTION}));
        register("list", define(baseRepoAccessor, new Attribute[]{NAME, DESCRIPTION, ENTITYSELECTOR}));
        register("edit", define(baseRepoAccessor, new Attribute[]{NAME, DESCRIPTION, MAINFORM}));
        register("form", define(baseRepoAccessor, new Attribute[]{ONSAVE}));
        register("datatable", define(baseRepoAccessor, new Attribute[]{ROUTE}));

        Attribute[] base = new Attribute[]{ID, VALUE, RENDER};
        Attribute[] input = new Attribute[]{LABEL, READONLY, CONVERTER, TYPE, INPUT_TYPE};
        Map<String, Attribute> baseInput = define(base, input);
        register("input", baseInput);
        register("checkbox", baseInput);
        register("text", baseInput);
        register("date", baseInput);

        register("column", define(base, new Attribute[]{HEADER_TEXT, FILTERING, ORDERING}));

        Map<String, Attribute> actionAttributes = define(new Attribute[]{ID, ROUTE, LABEL});
        register("action", actionAttributes);
        register("new", actionAttributes);
        register("update", actionAttributes);
        register("nav", actionAttributes);
        register("cancel", actionAttributes);
        register("delete", actionAttributes);

        register("link", define(base, new Attribute[]{ROUTE}));

    }

    private static Map<String, Attribute> define(Attribute[]... attributeSets) {
        Map<String, Attribute> atts = new HashMap();
        for (Attribute[] attSet : attributeSets) {
            for (Attribute att : attSet) {
                if (!atts.containsKey(att.name)) {
                    atts.put(att.name, att);
                }
            }
        }
        return atts;
    }


    public static void register(String name, Map<String, Attribute> atts) {
        registry.put(name, atts);
    }

    public static Map<String, Attribute> getDefinition(String name) {
        return registry.get(name);
    }


}