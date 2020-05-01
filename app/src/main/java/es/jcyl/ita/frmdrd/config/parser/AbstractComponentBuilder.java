package es.jcyl.ita.frmdrd.config.parser;
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

import es.jcyl.ita.frmdrd.config.builders.ComponentBuilder;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public abstract class AbstractComponentBuilder implements ComponentBuilder {

    protected ConfigConsole console;
    protected XmlPullParser xpp;
    protected String tagName;
    protected String id;

    protected boolean isAttributeSupported(String attName) {
        return true;
    }

    public final void withAttribute(String name, String value) {
        if (!isAttributeSupported(name)) {
            console.error(String.format("[line %s] Unsupported attribute: [%s] in component [%s@%s].", xpp.getLineNumber(), name, tagName, this.id));
        } else {
            doWithAttribute(name, value);
        }
    }

    abstract protected void doWithAttribute(String name, String value);


    @Override
    public void setConsole(ConfigConsole console) {
        this.console = console;
    }

    public void setXpp(XmlPullParser xpp) {
        this.xpp = xpp;
    }
}
