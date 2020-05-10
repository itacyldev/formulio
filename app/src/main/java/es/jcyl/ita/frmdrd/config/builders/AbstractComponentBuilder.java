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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.jcyl.ita.frmdrd.config.ComponentBuilder;
import es.jcyl.ita.frmdrd.config.ComponentBuilderFactory;
import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.meta.Attribute;
import es.jcyl.ita.frmdrd.config.meta.Attributes;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Builder that instantiates and element from and specific class and may extend the
 * building process with specific treatment of attributte values or child elements.
 */
public abstract class AbstractComponentBuilder<E> implements ComponentBuilder<E> {

    protected Map<String, Attribute> attributeDefs;
    private Class<? extends E> elementType;
    private ComponentBuilderFactory factory;
//    protected ComponentResolver resolver;

    public AbstractComponentBuilder(String tagName, Class<? extends E> clazz) {
        this.attributeDefs = Attributes.getDefinition(tagName);
        this.elementType = clazz;
    }

    @Override
    public E build(ConfigNode<E> node) {
        E element = instantiate();
        setAttributes(element, node);
//        setId(element);
        doConfigure(element, node);
        return element;
    }


//    /**
//     * Verifies that the element has the id attribute set or creates one based on element tag name.
//     */
//    protected void setId(E element) {
//        if (StringUtils.isBlank(element.getId())) {
//            // number elements with of current tag
//            String tag = this.node.getName();
//            Set<String> tags = this.resolver.getIdsForTag(tag);
//            element.setId(tag + tags.size() + 1); // table1, table2, table3,..
//        }
//    }

    /****** Extension points *******/

    protected E instantiate() {
        try {
            return (E) this.elementType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            String msg = String.format("Error while trying to instantiate element from class: " +
                    "[%s], make sure this class has a no-parameter constructor.", elementType.getName());
            error(msg, e);
            throw new ConfigurationException(msg, e);
        }
    }

    protected void setAttributes(E element, ConfigNode node) {
        Map<String, String> attributes = node.getAttributes();
        String tagName = node.getName();
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            String attName = entry.getKey();
            try {
                if (this.attributeDefs.get(attName).assignable) {
                    // set attribute using reflection
                    BeanUtils.setProperty(element, attName, entry.getValue());
                } else {
                    //TODO: create strategies per attribute?
                    // for now let the builder assume this responsability and reuse with helpers
                    doWithAttribute(element, attName, entry.getValue());
                }
            } catch (Exception e) {
                throw new ConfigurationException(error(String.format("Error while trying to set " +
                        "attribute [%s] on element [${tag}].", attName), e), e);
            }
        }
    }

    abstract protected void doWithAttribute(E element, String name, String value);

    protected abstract void doConfigure(E element, ConfigNode<E> node);


    /******* Helper methods ********/

    public List<ConfigNode> getChildren(ConfigNode root, String tagName) {
        List<ConfigNode> kids = new ArrayList<ConfigNode>();
        List<ConfigNode> children = root.getChildren();
        if (children != null) {
            for (ConfigNode n : children) {
                if (n.getName().equals(tagName)) {
                    kids.add(n);
                }
            }
        }
        return kids;
    }

    public List<ConfigNode> getNestedByTag(ConfigNode root, String tagName) {
        List<ConfigNode> result = new ArrayList<>();
        Set<String> set = new HashSet<>(Arrays.asList(tagName));
        findNestedByTag(root, set, result);
        return result;
    }

    public List<ConfigNode> getNestedByTag(ConfigNode root, Set<String> tagNames) {
        List<ConfigNode> result = new ArrayList<>();
        findNestedByTag(root, tagNames, result);
        return result;
    }

    /**
     * Recursively goes over the component tree storing fields in the given List
     */
    private void findNestedByTag(ConfigNode root, Set<String> tagNames, List<ConfigNode> found) {
        if (tagNames.contains(root.getName())) {
            found.add(root);
        } else {
            if (root.hasChildren()) {
                List<ConfigNode> children = root.getChildren();
                for (ConfigNode n : children) {
                    findNestedByTag(n, tagNames, found);
                }
            }
        }
    }
//    private find

    public UIComponent[] getUIChildren(ConfigNode root) {
        List<UIComponent> kids = new ArrayList<UIComponent>();
        List<ConfigNode> children = root.getChildren();
        if (children != null) {
            for (ConfigNode n : children) {
                if (n.getElement() instanceof UIComponent)
                    kids.add((UIComponent) n.getElement());
            }
        }
        return kids.toArray(new UIComponent[kids.size()]);
    }

    /**** internal work methods **/

    public void setFactory(ComponentBuilderFactory factory) {
        this.factory = factory;
    }

    protected ComponentBuilderFactory getFactory() {
        return this.factory;
    }


}
