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

import org.mini2Dx.beanutils.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.meta.Attribute;
import es.jcyl.ita.frmdrd.config.meta.Attributes;
import es.jcyl.ita.frmdrd.config.reader.BaseConfigNode;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.config.resolvers.ComponentResolver;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Builder that stores XML node information of the tag (attributes and nested childre).
 * It also validates given attributes against Attribute definition of current tag.
 */
public abstract class AbstractBuilder<E> implements ComponentBuilder {

    protected BaseConfigNode node;
    protected Map<String, Attribute> attributes;
    protected ComponentResolver resolver;

    public AbstractBuilder(String tagName) {
        this.attributes = Attributes.getDefinition(tagName);
        node = new BaseConfigNode();
        node.setName(tagName);
    }

    public final void withAttribute(String name, String value) {
        if (!isAttributeSupported(name)) {
            error(String.format("Unsupported attribute: [%s] in component ${tag}.", name));
        } else {
            node.getAttributes().put(name, value);
        }
    }

    public void setName(String tagName) {
        this.node.setName(tagName);
    }

    @Override
    public void addChild(String currentTag, ConfigNode component) {
        this.node.getChildren().add(component);
    }

    @Override
    public void addText(String text) {
        this.node.addText(text);
    }


    @Override
    public final ConfigNode build() {
        Object element = doBuild();
        node.setElement(element);
        return node;
    }


    /****** Extension points *******/

    protected boolean isAttributeSupported(String attName) {
        return (attributes == null) ? false : attributes.containsKey(attName);
    }

    protected abstract Object doBuild();

    /******* Helper methods ********/

    public String getAttribute(String name) {
        return this.node.getAttributes().get(name);
    }

    public List<ConfigNode> getChildren(String tagName) {
        List<ConfigNode> kids = new ArrayList<ConfigNode>();
        for (ConfigNode n : this.node.getChildren()) {
            if (n.getName().equals(tagName)) {
                kids.add(n);
            }
        }
        return kids;
    }

    public UIComponent[] getUIComponents() {
        List<UIComponent> kids = new ArrayList<UIComponent>();
        for (ConfigNode n : this.node.getChildren()) {
            if (n.getElement() instanceof UIComponent)
                kids.add((UIComponent) n.getElement());
        }
        return kids.toArray(new UIComponent[kids.size()]);
    }

    public void setResolver(ComponentResolver resolver) {
        this.resolver = resolver;
    }
}
