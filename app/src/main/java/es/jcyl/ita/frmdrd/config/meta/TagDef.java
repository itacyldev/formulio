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
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.DEFAULT_EXTENSION;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.DESCRIPTION;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.ENTITYSELECTOR;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.FILTERING;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.FOLDER;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.FORCE_SELECTION;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.HEADER_TEXT;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.HEIGHT;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.ID;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.INPUT_TYPE;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.LABEL;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.LABEL_EXPRESSION;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.LABEL_FILTERING_PROP;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.MAINFORM;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.NAME;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.NUM_VISIBLE_ROWS;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.ONSAVE;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.ORDERING;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.PROPERTIES;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.READONLY;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.REGISTER_IN_HISTORY;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.RENDER;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.REPO;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.ROUTE;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.TYPE;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.VALIDATOR;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.VALUE;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.VALUE_PROPERTY;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.WIDTH;

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
        register("datatable", define(baseRepoAccessor, new Attribute[]{ROUTE, NUM_VISIBLE_ROWS}));

        register("repo", define(new Attribute[]{ID, DBFILE, DBTABLE}));
        register("fileRepo", define(new Attribute[]{ID, FOLDER, DEFAULT_EXTENSION}));
        register("repofilter", define(new Attribute[]{ID, DBFILE, DBTABLE}));

        Attribute[] base = new Attribute[]{ID, VALUE, RENDER};
        Attribute[] input = new Attribute[]{LABEL, READONLY, CONVERTER, TYPE, INPUT_TYPE, VALIDATOR};
        Map<String, Attribute> baseInput = define(base, input);
        register("input", baseInput);
        register("checkbox", baseInput);
        register("text", baseInput);
        register("date", baseInput);
        register("image", define(baseInput, new Attribute[]{REPO, WIDTH, HEIGHT}));

        Map<String, Attribute> select = define(base, input, new Attribute[]{REPO, FORCE_SELECTION});
        register("select", select);
        register("autocomplete", select);
        register("options", define(new Attribute[]{VALUE_PROPERTY, LABEL_EXPRESSION, LABEL_FILTERING_PROP}));
        // attribute value in option element is a fixed value we don't need an expression
        Attribute optionValue = new Attribute("value");
        register("option", define(new Attribute[]{ID, optionValue, LABEL}));

        register("column", define(base, new Attribute[]{HEADER_TEXT, FILTERING, ORDERING}));

        Map<String, Attribute> actionAttributes = define(new Attribute[]{ID, ROUTE, LABEL, REGISTER_IN_HISTORY});
        register("action", actionAttributes);
        register("add", actionAttributes);
        register("update", actionAttributes);
        register("save", actionAttributes);
        register("cancel", actionAttributes);
        register("delete", actionAttributes);
        register("nav", actionAttributes);

        register("link", define(base, new Attribute[]{ROUTE}));

        register("tab", define(base, new Attribute[]{ID}));
        register("tabitem", define(base, new Attribute[]{ID, LABEL, PROPERTIES}));

        register("validator", define(base, new Attribute[]{TYPE}));
        register("param", define(base, new Attribute[]{NAME, VALUE}));
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

    private static Map<String, Attribute> define(Map<String, Attribute> baseAtts, Attribute[]... attributeSets) {
        Map<String, Attribute> atts = new HashMap();
        atts.putAll(baseAtts);
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

    public static boolean isDefinedTag(String tagName) {
        return registry.containsKey(tagName);
    }

    public static boolean supportsAttribute(String tagName, String attName) {
        if (!isDefinedTag(tagName)) {
            return false;
        }
        return getDefinition(tagName).containsKey(attName);
    }


}
