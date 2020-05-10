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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class AttributeDef {
    // component common attributes
    public static Attribute ID = new Attribute("id", true);
    public static Attribute VALUE = new Attribute("value", false);
    public static Attribute RENDER = new Attribute("render", false);
    // common input fields
    public static Attribute TYPE = new Attribute("type", true);
    public static Attribute LABEL = new Attribute("label", true);
    public static Attribute READONLY = new Attribute("readonly", true, Boolean.class);
    public static Attribute CONVERTER = new Attribute("converter", false);
    // component description
    public static Attribute NAME = new Attribute("name", true);
    public static Attribute DESCRIPTION = new Attribute("description", true);

    // edit view
    public static Attribute MAINFORM = new Attribute("mainForm", true);
    // list view
    public static Attribute ENTITYSELECTOR = new Attribute("entitySelector", true);

    // common trasversal parameters
    public static Attribute ROUTE = new Attribute("route", true);

    // repository definition
    public static Attribute REPO = new Attribute("repo", false);
    public static Attribute DBFILE = new Attribute("dbFile", true);
    public static Attribute DBTABLE = new Attribute("dbTable", true);
    public static Attribute PROPERTIES = new Attribute("properties", true);

    // event handling
    public static Attribute ONSAVE = new Attribute("onsave", true);


}
