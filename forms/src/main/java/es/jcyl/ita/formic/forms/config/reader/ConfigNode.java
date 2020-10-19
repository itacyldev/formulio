package es.jcyl.ita.formic.forms.config.reader;
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

import androidx.annotation.NonNull;

import org.mini2Dx.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Object created by a builder during config reading process.
 */
public class ConfigNode<E> {
    private String name;
    private Map<String, String> attributes;
    private List<ConfigNode> children;
    private List<String> texts;
    // related object built from current node information
    private E element;
    private ConfigNode parent;

    public ConfigNode(String tag) {
        this.name = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public E getElement() {
        return element;
    }

    public void setElement(E element) {
        this.element = element;
    }

    public Map<String, String> getAttributes() {
        if (attributes == null) {
            this.attributes = new HashMap<>();
        }
        return attributes;
    }

    public String getId() {
        return getAttribute("id");
    }

    public void setAttribute(String name, String value, String converter) {
        if (attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.put(name, value);
    }

    public void setAttribute(String name, String value) {
        setAttribute(name, value, null);
    }

    public String getAttribute(String name) {
        return (attributes == null) ? null : this.attributes.get(name);
    }

    public boolean hasChildren() {
        return CollectionUtils.isNotEmpty(this.children);
    }

    public List<ConfigNode> getChildren() {
        if (children == null) {
            this.children = new ArrayList<>();
        }
        return children;
    }

    public void addChild(ConfigNode kid) {
        if (children == null) {
            this.children = new ArrayList<>();
        }
        kid.setParent(this);
        this.children.add(kid);
    }

    public void setChildren(List<ConfigNode> kids) {
        this.children = kids;
        for (ConfigNode n : kids) {
            n.setParent(this);
        }
    }

    public void addText(String text) {
        if (this.texts == null) {
            this.texts = new ArrayList<>();
        }
        this.texts.add(text);
    }

    public List<String> getTexts() {
        return texts;
    }

    public void setId(String id) {
        if (attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.put("id", id);
    }

    public ConfigNode copy() {
        return copy(false, false);
    }

    public ConfigNode copy(boolean copyDependants, boolean copyAscendant) {
        ConfigNode n = new ConfigNode(this.getName());
        n.getAttributes().putAll(this.attributes);
        if (copyDependants) {
            n.setChildren(this.children);
        }
        if (copyAscendant) {
            n.setParent(this.parent);
        }
        return n;
    }

    public boolean hasAttribute(String attName) {
        return attributes.containsKey(attName);
    }

    public ConfigNode getParent() {
        return parent;
    }

    public void setParent(ConfigNode parent) {
        this.parent = parent;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("<%s - %s/>]", this.name, this.attributes);
    }
}
