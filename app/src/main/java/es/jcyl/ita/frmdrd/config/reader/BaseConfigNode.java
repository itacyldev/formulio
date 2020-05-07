package es.jcyl.ita.frmdrd.config.reader;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Default implementation of ConfigNode interface
 */
public class BaseConfigNode implements ConfigNode {

    private String name;
    private Object element;
    private Map<String, String> attributes;
    private List<ConfigNode> children;
    private List<String> texts;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object getElement() {
        return element;
    }

    public void setElement(Object element) {
        this.element = element;
    }

    @Override
    public Map<String, String> getAttributes() {
        if (attributes == null) {
            this.attributes = new HashMap<>();
        }
        return attributes;
    }

    @Override
    public String getAttribute(String name) {
        return this.attributes.get(name);
    }

    @Override
    public List<ConfigNode> getChildren() {
        if (children == null) {
            this.children = new ArrayList<>();
        }
        return children;
    }

    public void addText(String text) {
        if (this.texts == null) {
            this.texts = new ArrayList<>();
        }
        this.texts.add(text);
    }

    @Override
    public List<String> getTexts() {
        return texts;
    }
}
