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
import es.jcyl.ita.frmdrd.config.parser.ConfigNode;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public interface ComponentBuilder {

    void withAttribute(String name, String value);

    ConfigNode build();

    void addText(String text);

    void addChild(String currentTag, ConfigNode component);

    void setConsole(ConfigConsole console);
    void setParser(XmlPullParser xpp);
}
