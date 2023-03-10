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
    public static final Attribute ID = new Attribute("id");
    public static final Attribute VALUE = new Attribute("value", "valueExpression", "binding");
    public static final Attribute RENDER = new Attribute("render", "renderExpression", "binding");
    public static final Attribute READONLY = new Attribute("readonly", "readonly", "binding");
    public static final Attribute READONLY_MESSAGE = new Attribute("readonlyMessage", "readonlyMessage", String.class);
    public static final Attribute ALLOWS_PARTIAL_RESTORE = new Attribute("allowsPartialRestore", Boolean.class);

    // common input fields
    public static final Attribute TYPE = new Attribute("type", "type", String.class);
    public static final Attribute TYPE_STR = new Attribute("type", "typeStr", String.class);
    public static final Attribute LABEL = new Attribute("label");
    public static final Attribute PLACEHOLDER = new Attribute("placeHolder", "placeHolder", "binding");

    public static final Attribute CONVERTER = new Attribute("converter", "valueConverter", String.class);
    // component description
    public static final Attribute NAME = new Attribute("name");
    public static final Attribute DESCRIPTION = new Attribute("description");
    public static final Attribute MAIN_FORM = new Attribute("mainForm", Boolean.class);

    // edit view
    public static final Attribute MAINFORM = new Attribute("mainForm");
    // list view
    public static final Attribute ENTITYSELECTOR = new Attribute("entityList");

    // column
    public static final Attribute HEADER_TEXT = new Attribute("headerText");
    public static final Attribute FILTERING = new Attribute("filtering", Boolean.class);
    public static final Attribute ORDERING = new Attribute("ordering", Boolean.class);

    // input
    public static final Attribute INPUT_TYPE = new Attribute("inputType", Integer.class);
    public static final Attribute VALIDATOR = new Attribute("validator", "validator", "validator");
    public static final Attribute LINES = new Attribute("lines", Integer.class);
    public static final Attribute HINT = new Attribute("hint", String.class);
    public static final Attribute HAS_DELETE_BUTTON = new Attribute("hasDeleteButton", Boolean.class);
    public static final Attribute HAS_TODAY_BUTTON = new Attribute("hasTodayButton", Boolean.class);
    public static final Attribute PATTERN = new Attribute("pattern", String.class);

    // repository definition
    public static final Attribute REPO = new Attribute("repo", "repo", "repo");
    public static final Attribute DBFILE = new Attribute("dbFile", "dbFile", "pathResolver");
    public static final Attribute DBTABLE = new Attribute("dbTable", true);
    public static final Attribute PROPERTIES = new Attribute("properties", true);
    public static final Attribute COLUMN_NAME = new Attribute("columnName", true);
    public static final Attribute EXPRESSION = new Attribute("expression", true);
    public static final Attribute EXPRESSION_TYPE = new Attribute("expressionType", true);
    public static final Attribute EVAL_ON = new Attribute("evalOn", true);
    public static final Attribute PROPERTY = new Attribute("property", true);
    public static final Attribute REPO_PROPERTY = new Attribute("repoProperty", true);
    public static final Attribute KEYGENERATOR = new Attribute("keyGenerator", true);

    // autocomplete
    public static final Attribute VALUE_PROPERTY = new Attribute("valueProperty");
    public static final Attribute LABEL_EXPRESSION = new Attribute("labelExpression", "labelExpression", "binding");
    public static final Attribute LABEL_FILTERING_PROP = new Attribute("labelFilteringProperty");
    public static final Attribute FORCE_SELECTION = new Attribute("forceSelection", Boolean.class);

    // datatable, datalist
    public static final Attribute NUM_VISIBLE_ROWS = new Attribute("numVisibleRows", Integer.class);

    // image gallery
    public static final Attribute COLUMNS = new Attribute("columns", Integer.class);

    // card
    public static final Attribute TEMPLATE = new Attribute("template", true);
    public static final Attribute TITLE = new Attribute("title", true);
    public static final Attribute SUBTITLE = new Attribute("subtitle", true);
    public static final Attribute IMAGE = new Attribute("image", true);

    public static final Attribute IMAGE_POSITION = new Attribute("imagePosition", "imagePosition", String.class);
    public static final Attribute EXPANDED = new Attribute("expanded", true);
    public static final Attribute EXPANDABLE = new Attribute("expandable", true);

    // tab
    public static final Attribute SELECTED = new Attribute("selected", "selected", "binding");

    // style, layout, ...
    public static final Attribute HEIGHT = new Attribute("height", Integer.class);
    public static final Attribute WIDTH = new Attribute("width", Integer.class);
    public static final Attribute EMBEDDED = new Attribute("embedded", Boolean.class);
    public static final Attribute ORIENTATION = new Attribute("orientation", String.class);

    public static final Attribute FOLDER = new Attribute("folder", String.class);
    public static final Attribute DEFAULT_EXTENSION = new Attribute("defaultExtension", String.class);

    public static final Attribute COLSPANS = new Attribute("colspans", String.class);

    public static final Attribute WEIGHTS = new Attribute("weights", String.class);
    public static final Attribute BORDER = new Attribute("border", Boolean.class);

    // text style
    public static final Attribute FONT_SIZE = new Attribute("fontSize", String.class);
    public static final Attribute FONT_COLOR = new Attribute("fontColor", "fontColor", "color");
    public static final Attribute FONT_FAMILY = new Attribute("fontFamily", String.class);
    public static final Attribute BACKGROUND_COLOR = new Attribute("backgroundColor", "backgroundColor", "color");
    public static final Attribute BOLD = new Attribute("bold", Boolean.class);
    public static final Attribute ITALIC = new Attribute("italic", Boolean.class);
    public static final Attribute UPPERCASE = new Attribute("uppercase", Boolean.class);
    public static final Attribute UNDERLINED = new Attribute("underlined", Boolean.class);
    public static final Attribute COLOR = new Attribute("color", "color", "color");
    public static final Attribute STROKE_WIDTH = new Attribute("strokeWidth", Integer.class);

    // Action parameters
    public static final Attribute ACTION = new Attribute("action", "action", "action");
    public static final Attribute ROUTE = new Attribute("route", "route", "binding-route");
    public static final Attribute CONTROLLER = new Attribute("controller", String.class);
    public static final Attribute REGISTER_IN_HISTORY = new Attribute("registerInHistory", Boolean.class);
    public static final Attribute REFRESH = new Attribute("refresh", String.class);
    public static final Attribute RESTORE_VIEW = new Attribute("restoreView", Boolean.class);
    public static final Attribute POP_HISTORY = new Attribute("popHistory", Integer.class);
    public static final Attribute MESSAGE = new Attribute("message", "message", String.class);
    public static final Attribute CONFIRMATION = new Attribute("confirmation", "confirmation", "binding");
    public static final Attribute LABEL_CONFIRMATION = new Attribute("labelConfirmation", "labelConfirmation", String.class);
    public static final Attribute METHOD = new Attribute("method", String.class);

    // scripting
    public static final Attribute ON_SAVE = new Attribute("onsave", true);
    public static final Attribute ON_BEFORE_RENDER = new Attribute("onBeforeRender", "onBeforeRenderAction", String.class);
    public static final Attribute ON_AFTER_RENDER = new Attribute("onAfterRender", "onAfterRenderAction", String.class);
    public static final Attribute SRC = new Attribute("src", "src", "pathResolver");
}
