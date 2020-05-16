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

import es.jcyl.ita.frmdrd.el.ValueBindingExpression;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class AttributeDef {
    // component common attributes
    public static Attribute ID = new Attribute("id");
    public static Attribute VALUE = new Attribute("value", "value", "binding");
    // TODO: fix this workaround and set option.value as a ValueBindingExpression #204221
    public static Attribute CODE = new Attribute("code", "value", String.class); // alternative to value with no expression
    public static Attribute RENDER = new Attribute("render", "value", "binding");
    // common input fields
//    public static Attribute TYPE = new Attribute("type", true);
    public static Attribute LABEL = new Attribute("label");
    public static Attribute READONLY = new Attribute("readonly", Boolean.class);
    public static Attribute CONVERTER = new Attribute("converter", false);
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

    // common trasversal parameters
    public static Attribute ROUTE = new Attribute("route", true);

    // repository definition
    public static Attribute REPO = new Attribute("repo", "repo", "repo");
    public static Attribute DBFILE = new Attribute("dbFile", "dbFile", "pathResolver");
    public static Attribute DBTABLE = new Attribute("dbTable", true);
    public static Attribute PROPERTIES = new Attribute("properties", true);

    // autocomplete
    public static Attribute VALUE_PROPERTY = new Attribute("valueProperty");
    public static Attribute LABEL_EXPRESSION = new Attribute("labelExpression", "labelExpression", "binding");
    public static Attribute LABEL_FILTERING_PROP = new Attribute("labelFilteringProperty");
    public static Attribute FORCE_SELECTION = new Attribute("forceSelection", Boolean.class);

    // event handling
    public static Attribute ONSAVE = new Attribute("onsave", true);


}
