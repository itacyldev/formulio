package es.jcyl.ita.formic.forms.config.meta;
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

import org.mini2Dx.collections.MapUtils;

import java.util.HashMap;
import java.util.Map;

import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.ACTION;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.ALLOWS_PARTIAL_RESTORE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.BACKGROUND_COLOR;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.BOLD;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.BORDER;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.COLOR;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.COLSPANS;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.COLUMN_NAME;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.CONFIRMATION;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.CONTROLLER;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.CONVERTER;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.DBFILE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.DBTABLE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.DEFAULT_EXTENSION;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.DESCRIPTION;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.EMBEDDED;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.ENTITYSELECTOR;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.EVAL_ON;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.EXPANDABLE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.EXPANDED;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.EXPRESSION;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.EXPRESSION_TYPE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.FILTERING;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.FOLDER;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.FONT_COLOR;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.FONT_FAMILY;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.FONT_SIZE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.FORCE_SELECTION;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.HAS_DELETE_BUTTON;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.HAS_TODAY_BUTTON;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.HEADER_TEXT;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.HEIGHT;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.HINT;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.ID;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.IMAGE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.IMAGE_POSITION;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.INPUT_TYPE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.ITALIC;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.KEYGENERATOR;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.LABEL;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.LABEL_CONFIRMATION;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.LABEL_EXPRESSION;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.LABEL_FILTERING_PROP;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.LINES;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.MAINFORM;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.MAIN_FORM;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.MESSAGE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.METHOD;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.NAME;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.NUM_VISIBLE_ROWS;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.ON_AFTER_RENDER;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.ON_BEFORE_RENDER;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.ON_SAVE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.ORDERING;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.ORIENTATION;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.PATTERN;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.PLACEHOLDER;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.POP_HISTORY;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.PROPERTIES;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.PROPERTY;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.READONLY;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.READONLY_MESSAGE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.REFRESH;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.REGISTER_IN_HISTORY;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.RENDER;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.REPO;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.REPO_PROPERTY;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.RESTORE_VIEW;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.ROUTE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.SELECTED;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.SRC;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.STROKE_WIDTH;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.SUBTITLE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.TEMPLATE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.TITLE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.TYPE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.TYPE_STR;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.UNDERLINED;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.UPPERCASE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.VALIDATOR;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.VALUE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.VALUE_PROPERTY;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.WEIGHTS;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.WIDTH;

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

    private static String ACTION_TAGS[] = {"action", "add", "create", "update", "save", "cancel", "delete", "nav", "js"};

    private static String BASE_TAGS[] = {"main", "list", "edit", "view", "form"};

    private static void initialize() {
        Attribute[] scriptHooks = new Attribute[]{ON_BEFORE_RENDER, ON_AFTER_RENDER};

        Attribute[] baseRepoAccessor = new Attribute[]{ID, PROPERTIES, REPO, DBFILE, DBTABLE, ON_BEFORE_RENDER,
                ON_AFTER_RENDER, ALLOWS_PARTIAL_RESTORE};
        register("main", define(baseRepoAccessor, new Attribute[]{NAME, DESCRIPTION, MAIN_FORM}));
        register("list", define(baseRepoAccessor, new Attribute[]{NAME, DESCRIPTION, ENTITYSELECTOR}));
        register("edit", define(baseRepoAccessor, new Attribute[]{NAME, DESCRIPTION, MAINFORM}));
        register("view", define(baseRepoAccessor, new Attribute[]{NAME, DESCRIPTION, ENTITYSELECTOR, MAINFORM}));
        register("form", define(baseRepoAccessor, new Attribute[]{ON_SAVE}));

        register("datatable", define(baseRepoAccessor, new Attribute[]{ROUTE, NUM_VISIBLE_ROWS, ACTION}));
        register("datalist", define(baseRepoAccessor, new Attribute[]{ROUTE, NUM_VISIBLE_ROWS, ACTION, TEMPLATE}));
        register("datalistitem", define(scriptHooks, new Attribute[]{ID}));
        register("card", define(scriptHooks, new Attribute[]{ID, TEMPLATE, TITLE, SUBTITLE, IMAGE, LABEL, DESCRIPTION,
                EXPANDED, EXPANDABLE, IMAGE_POSITION, ON_BEFORE_RENDER, ON_AFTER_RENDER, ACTION, ALLOWS_PARTIAL_RESTORE}));

        register("repo", define(new Attribute[]{ID, DBFILE, DBTABLE}));
        register("fileRepo", define(new Attribute[]{ID, FOLDER, DEFAULT_EXTENSION}));
        register("memoRepo", define(new Attribute[]{ID, PROPERTIES}));

        register("repofilter", define(new Attribute[]{ID}));
        register("keyGenerator", define(new Attribute[]{TYPE}));
        register("meta", define(new Attribute[]{PROPERTIES, KEYGENERATOR}));
        register("property", define(new Attribute[]{NAME, EXPRESSION, COLUMN_NAME, EXPRESSION_TYPE, CONVERTER, EVAL_ON}));
        register("mapping", define(new Attribute[]{ID, REPO, PROPERTY,
                new Attribute("fk", String.class, false),
                new Attribute("insertable", Boolean.class),
                new Attribute("updatable", Boolean.class),
                new Attribute("deletable", Boolean.class),
                new Attribute("retrieveMeta", Boolean.class)
        }));

        Attribute[] base = new Attribute[]{ID, VALUE, RENDER, READONLY, READONLY_MESSAGE, ON_BEFORE_RENDER,
                ON_AFTER_RENDER, ACTION, ALLOWS_PARTIAL_RESTORE};
        Attribute[] input = new Attribute[]{LABEL, CONVERTER, TYPE_STR, INPUT_TYPE, VALIDATOR, HAS_DELETE_BUTTON,
                HAS_TODAY_BUTTON, PLACEHOLDER, PATTERN, HINT};
        Map<String, Attribute> baseInput = define(base, input);
        register("input", baseInput);
        register("checkbox", baseInput);
        register("switcher", baseInput);
        register("text", baseInput);
        register("date", baseInput);
        register("datetime", baseInput);
        register("textarea", define(base, input, new Attribute[]{LINES}));
        register("image", define(baseInput, new Attribute[]{REPO, EMBEDDED, WIDTH, HEIGHT, REPO_PROPERTY}));

        Map<String, Attribute> select = define(base, input, new Attribute[]{REPO, FORCE_SELECTION});
        register("select", select);
        register("autocomplete", select);
        register("radio", define(select, new Attribute[]{ORIENTATION, WEIGHTS}));
        register("options", define(new Attribute[]{VALUE_PROPERTY, LABEL_EXPRESSION, LABEL_FILTERING_PROP}, scriptHooks));
        // attribute value in option element is a fixed value we don't need an expression
        Attribute optionValue = new Attribute("value");
        register("option", define(new Attribute[]{ID, optionValue, LABEL}, scriptHooks));

        register("buttonbar", define(base, new Attribute[]{TYPE}));
        register("button", define(baseInput, new Attribute[]{ROUTE, CONFIRMATION, LABEL_CONFIRMATION}));
        register("link", define(baseInput, new Attribute[]{ROUTE}));

        Map<String, Attribute> actionAttributes = define(new Attribute[]{ID, ROUTE, LABEL, TYPE,
                CONTROLLER, REGISTER_IN_HISTORY, REFRESH, RESTORE_VIEW, MESSAGE, POP_HISTORY});
        register("action", actionAttributes);
        register("add", actionAttributes);
        register("create", actionAttributes);
        register("update", actionAttributes);
        register("save", actionAttributes);
        register("cancel", actionAttributes);
        register("delete", actionAttributes);
        register("nav", actionAttributes);
        register("js", define(actionAttributes, new Attribute[]{METHOD}));

        register("tab", define(base, new Attribute[]{ID, ALLOWS_PARTIAL_RESTORE}));
        register("tabitem", define(base, new Attribute[]{ID, LABEL, PROPERTIES, SELECTED}));

        register("table", define(base, new Attribute[]{ID, HEADER_TEXT, WEIGHTS, BORDER}));
        register("row", define(base, new Attribute[]{ID, LABEL, PROPERTIES, COLSPANS, WEIGHTS}, scriptHooks));
        register("column", define(base, new Attribute[]{HEADER_TEXT, FILTERING, ORDERING}));

        register("validator", define(base, new Attribute[]{TYPE_STR}));
        register("param", define(base, new Attribute[]{NAME, VALUE}));

        register("script", define(new Attribute[]{SRC}));

        Attribute[] text = new Attribute[]{FONT_SIZE, FONT_COLOR, FONT_FAMILY, BACKGROUND_COLOR, ITALIC, BOLD, UPPERCASE, UNDERLINED};
        register("p", define(base, text, scriptHooks, new Attribute[]{ID, NAME, VALUE}));
        register("divisor", define(base, new Attribute[]{COLOR, STROKE_WIDTH}));

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
        registry.put(name.toUpperCase(), atts);
    }

    public static Map<String, Attribute> getDefinition(String name) {
        return registry.get(name.toUpperCase());
    }

    public static Map<String, Map<String, Attribute>> getDefinitions() {
        return MapUtils.unmodifiableMap(registry);
    }

    public static boolean isDefinedTag(String tagName) {
        return registry.containsKey(tagName.toUpperCase());
    }

    public static boolean supportsAttribute(String tagName, String attName) {
        if (!isDefinedTag(tagName)) {
            return false;
        }
        return getDefinition(tagName).containsKey(attName);
    }

    public static boolean isActionTag(String tagName) {
        for (String tag : ACTION_TAGS) {
            if (tagName.toLowerCase().equals(tag)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBaseTag(String tagName) {
        for (String tag : BASE_TAGS) {
            if (tagName.toLowerCase().equals(tag)) {
                return true;
            }
        }
        return false;
    }
}
