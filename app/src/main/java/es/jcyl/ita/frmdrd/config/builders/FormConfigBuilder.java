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

import es.jcyl.ita.frmdrd.config.FormConfig;
import es.jcyl.ita.frmdrd.config.reader.AbstractComponentBuilder;
import es.jcyl.ita.frmdrd.config.reader.BaseConfigNode;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class FormConfigBuilder extends AbstractComponentBuilder {
    FormConfig config;

    public FormConfigBuilder() {
        super("form");
        config = new FormConfig("");
    }

    @Override
    protected void doWithAttribute(String name, String value) {
        if ("name".equals(name)) {
            config.setName(name);
        }
    }

    @Override
    public BaseConfigNode build() {
        BaseConfigNode node = new BaseConfigNode();
        node.setName(this.tagName);
        node.setElement(this.config);
        return node;
    }

    @Override
    public void addText(String text) {

    }

    @Override
    public void addChild(String currentTag, BaseConfigNode component) {

    }
}
