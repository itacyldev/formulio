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
    // common input fields
//    public static Attribute TYPE = new Attribute("type", true);
    public static Attribute LABEL = new Attribute("label");
    public static Attribute LABELS = new Attribute("labels");
    public static Attribute READONLY = new Attribute("readOnly", Boolean.class);
    public static Attribute CONVERTER = new Attribute("converter", "valueConverter", String.class);
    // component description
    public static Attribute NAME = new Attribute("name");
    public static Attribute DESCRIPTION = new Attribute("description");

    // edit view
    public static Attribute MAINFORM = new Attribute("mainForm");
    // list view
    public static Attribute ENTITYSELECTOR = new Attribute("entitySelector");

    // column
    public static Attribute HEADER_TEXT = new Attribute("headerText");
    public static Attribute FILTERING = new Attribute("filtering", Boolean.class);
    public static Attribute ORDERING = new Attribute("ordering", Boolean.class);

    // input
    public static Attribute TYPE = new Attribute("type", "typeStr", String.class);
    public static Attribute INPUT_TYPE = new Attribute("inputType", Integer.class);
    public static Attribute VALIDATOR = new Attribute("validator", "validator", "validator");
    public static Attribute LINES = new Attribute("lines", Integer.class);

    public static Attribute DEFAULT_VALUE = new Attribute("defaultValue", String.class);

    // common trasversal parameters
    public static Attribute ROUTE = new Attribute("route", true);
    public static Attribute REGISTER_IN_HISTORY = new Attribute("registerInHistory", Boolean.class);


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

    public static final Attribute EXPANDED = new Attribute("expanded", true);
    public static final Attribute EXPANDABLE = new Attribute("expandable", true);

    // event handling
    public static Attribute ONSAVE = new Attribute("onsave", true);

    // style, layout, ...
    public static Attribute HEIGHT = new Attribute("height", Integer.class);
    public static Attribute WIDTH = new Attribute("width", Integer.class);
    public static Attribute EMBEDDED = new Attribute("embedded", Boolean.class);
    public static Attribute ORIENTATION = new Attribute("orientation", String.class);

    public static Attribute FOLDER = new Attribute("folder", String.class);
    public static Attribute DEFAULT_EXTENSION = new Attribute("defaultExtension", String.class);

    public static Attribute COLSPANS = new Attribute("colspans", String.class);

}
