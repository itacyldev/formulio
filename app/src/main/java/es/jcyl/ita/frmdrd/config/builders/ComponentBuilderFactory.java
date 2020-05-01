package es.jcyl.ita.frmdrd.config.builders;
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

import org.xmlpull.v1.XmlPullParser;

import es.jcyl.ita.frmdrd.config.parser.ConfigConsole;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ComponentBuilderFactory {

    private XmlPullParser xpp;
    private final ConfigConsole console = new ConfigConsole();
    private static ComponentBuilderFactory _instance;

    public static ComponentBuilderFactory getInstance() {
        if (_instance == null) {
            _instance = new ComponentBuilderFactory();
        }
        return _instance;
    }

    public ComponentBuilder getBuilder(String tagName) {
        ComponentBuilder builder = null;
        if(tagName.equalsIgnoreCase("form")){
            return new FormControllerBuilder();

        }
        builder.setConsole(console);
        builder.setParser(xpp);
        return builder;

    }

    public void setXmlParser(XmlPullParser xpp) {
        this.xpp = xpp;
    }
}
