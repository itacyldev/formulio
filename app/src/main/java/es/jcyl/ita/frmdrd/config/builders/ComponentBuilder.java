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

import es.jcyl.ita.frmdrd.config.ConfigConsole;
import es.jcyl.ita.frmdrd.config.reader.BaseConfigNode;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Creates a UIComponent from the read xml configuration
 */
public interface ComponentBuilder {

    /**
     * stores current tagName
     */
    void setName(String name);

    /**
     * node creation process
     */
    void withAttribute(String name, String value);

    void addText(String text);

    void addChild(String childTag, ConfigNode component);

    ConfigNode build();

}
