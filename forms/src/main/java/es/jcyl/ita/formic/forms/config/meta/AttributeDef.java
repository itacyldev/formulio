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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Component attribute catalog
 */
public class AttributeDef {
    // component common attributes
    public static Attribute ID = new Attribute("id");
    public static Attribute VALUE = new Attribute("value", "valueExpression", "binding");
    public static Attribute RENDER = new Attribute("render", "renderExpression", "binding");
    public static Attribute READONLY = new Attribute("readOnly", "readOnly", "binding");
    public static Attribute READONLY_MESSAGE = new Attribute("readOnlyMessage", "readOnlyMessage", String.class);

    // common input fields
    public static Attribute TYPE = new Attribute("type", "type", String.class);
    public static Attribute TYPE_STR = new Attribute("type", "typeStr", String.class);
    public static Attribute LABEL = new Attribute("label");
    public static Attribute PLACEHOLDER = new Attribute("placeHolder", "placeHolder", "binding");

    public static Attribute CONVERTER = new Attribute("converter", "valueConverter", String.class);
    // component description
    public static Attribute NAME = new Attribute("name");
    public static Attribute DESCRIPTION = new Attribute("description");

    // edit view
    public static Attribute MAINFORM = new Attribute("mainForm");
    // list view
    public static Attribute ENTITYSELECTOR = new Attribute("entityList");

    // column
    public static Attribute HEADER_TEXT = new Attribute("headerText");
    public static Attribute FILTERING = new Attribute("filtering", Boolean.class);
    public static Attribute ORDERING = new Attribute("ordering", Boolean.class);

    // input
    public static Attribute INPUT_TYPE = new Attribute("inputType", Integer.class);
    public static Attribute VALIDATOR = new Attribute("validator", "validator", "validator");
    public static Attribute LINES = new Attribute("lines", Integer.class);

    public static Attribute DEFAULT_VALUE = new Attribute("defaultValue", String.class);

    public static Attribute HINT = new Attribute("hint");

    public static Attribute HAS_DELETE_BUTTON = new Attribute("hasDeleteButton", Boolean.class);

    public static Attribute HAS_TODAY_BUTTON = new Attribute("hasTodayButton", Boolean.class);

    // repository definition
    public static Attribute REPO = new Attribute("repo", "repo", "repo");
    public static Attribute DBFILE = new Attribute("dbFile", "dbFile", "pathResolver");
    public static Attribute DBTABLE = new Attribute("dbTable", true);
    public static Attribute PROPERTIES = new Attribute("properties", true);
    public static Attribute COLUMN_NAME = new Attribute("columnName", true);
    public static Attribute EXPRESSION = new Attribute("expression", true);
    public static Attribute EXPRESSION_TYPE = new Attribute("expressionType", true);
    public static Attribute EVAL_ON = new Attribute("evalOn", true);
    public static Attribute PROPERTY = new Attribute("property", true);
    public static Attribute REPO_PROPERTY = new Attribute("repoProperty", true);

    // autocomplete
    public static Attribute VALUE_PROPERTY = new Attribute("valueProperty");
    public static Attribute LABEL_EXPRESSION = new Attribute("labelExpression", "labelExpression", "binding");
    public static Attribute LABEL_FILTERING_PROP = new Attribute("labelFilteringProperty");
    public static Attribute FORCE_SELECTION = new Attribute("forceSelection", Boolean.class);

    // datatable, datalist
    public static Attribute NUM_VISIBLE_ROWS = new Attribute("numVisibleRows", Integer.class);

    // card
    public static Attribute TEMPLATE = new Attribute("template", true);
    public static final Attribute TITLE = new Attribute("title", true);
    public static final Attribute SUBTITLE = new Attribute("subtitle", true);
    public static final Attribute IMAGE = new Attribute("image", true);

    public static final Attribute IMAGE_POSITION = new Attribute("imagePosition", "imagePosition", String.class);
    public static final Attribute EXPANDED = new Attribute("expanded", true);
    public static final Attribute EXPANDABLE = new Attribute("expandable", true);

    // tab
    public static Attribute SELECTED = new Attribute("selected", "selected", "binding");

    // style, layout, ...
    public static Attribute HEIGHT = new Attribute("height", Integer.class);
    public static Attribute WIDTH = new Attribute("width", Integer.class);
    public static Attribute EMBEDDED = new Attribute("embedded", Boolean.class);
    public static Attribute ORIENTATION = new Attribute("orientation", String.class);

    public static Attribute FOLDER = new Attribute("folder", String.class);
    public static Attribute DEFAULT_EXTENSION = new Attribute("defaultExtension", String.class);

    public static Attribute COLSPANS = new Attribute("colspans", String.class);

    public static Attribute WEIGHTS = new Attribute("weights", String.class);
    public static Attribute BORDER = new Attribute("border", Boolean.class);

    // text style
    public static Attribute FONT_SIZE = new Attribute("fontSize", String.class);
    public static Attribute FONT_COLOR = new Attribute("fontColor", "fontColor", "color");
    public static Attribute FONT_FAMILY = new Attribute("fontFamily", String.class);
    public static Attribute BACKGROUND_COLOR = new Attribute("backGroundColor", String.class);
    public static Attribute BOLD = new Attribute("bold", Boolean.class);
    public static Attribute ITALIC = new Attribute("italic", Boolean.class);
    public static Attribute UPPERCASE = new Attribute("uppercase", Boolean.class);
    public static Attribute UNDERLINED = new Attribute("underlined", Boolean.class);
    public static Attribute COLOR = new Attribute("color", "color", "color");
    public static Attribute STROKE_WIDTH = new Attribute("strokeWidth", Integer.class);

    // Action parameters
    public static Attribute ACTION = new Attribute("action", "action", "action");
    public static Attribute ROUTE = new Attribute("route", "route", "binding-route");
    public static Attribute REGISTER_IN_HISTORY = new Attribute("registerInHistory", Boolean.class);
    public static Attribute REFRESH = new Attribute("refresh", String.class);
    public static Attribute MESSAGE = new Attribute("message", "message", String.class);

    // scripting
    public static Attribute ON_SAVE = new Attribute("onsave", true);
    public static Attribute ON_BEFORE_RENDER = new Attribute("onBeforeRender", "onBeforeRenderAction", String.class);
    public static Attribute ON_AFTER_RENDER = new Attribute("onAfterRender", "onAfterRenderAction", String.class);
    public static Attribute SRC = new Attribute("src", "src", "pathResolver");

}
