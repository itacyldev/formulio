package es.jcyl.ita.formic.forms.config.builders;
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
import org.mini2Dx.beanutils.ConvertUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import es.jcyl.ita.formic.forms.config.AttributeResolver;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.builders.proxy.UIComponentProxyBuilder;
import es.jcyl.ita.formic.forms.config.meta.Attribute;
import es.jcyl.ita.formic.forms.config.meta.AttributeDef;
import es.jcyl.ita.formic.forms.config.meta.TagDef;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Builder that instantiates and element from and specific class and may extend the
 * building process with specific treatment of attributte values or child elements.
 * This abstract class provides common methods for form and repo componentes building.
 */
public abstract class AbstractComponentBuilder<E> implements ComponentBuilder<E> {

    protected Map<String, Attribute> attributeDefs;
    private Class<? extends E> elementType;
    private ComponentBuilderFactory factory;
    private UIComponentProxyBuilder proxyBuilder;


    public AbstractComponentBuilder(String tagName, Class<? extends E> clazz) {
        this.attributeDefs = TagDef.getDefinition(tagName);
        if (attributeDefs == null) {
            throw new ConfigurationException(error(String.format("No attribute definition for tag" +
                    " [%s], review the TagDef class and register the new component.", tagName)));
        }
        this.elementType = clazz;
    }

    @Override
    public E build(ConfigNode<E> node) {
        E element = instantiate();
        Map<String, String> expressionAtts = new HashMap<>();
        if (element != null) {
            setAttributes(element, node, expressionAtts);
        }
        if(!expressionAtts.isEmpty()){
            element = proxify(element);
        }
        node.setElement(element);
        setupOnSubtreeStarts(node);
        return element;
    }

    private E proxify(E element) {
        return element;
    }

    @Override
    public final void processChildren(ConfigNode<E> node) {
        setupOnSubtreeEnds(node);
    }

    /****** Extension points *******/

    protected void doWithAttribute(E element, String name, String value) {
    }//empty implementation

    protected abstract void setupOnSubtreeStarts(ConfigNode<E> node);

    protected abstract void setupOnSubtreeEnds(ConfigNode<E> node);


    protected E instantiate() {
        if (this.elementType == null) {
            // no instance needed
            return null;
        }
        try {
            return (E) this.elementType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            String msg = String.format("Error while trying to instantiate element from class: " +
                    "[%s], make sure this class has a no-parameter constructor.", elementType.getName());
            error(msg, e);
            throw new ConfigurationException(msg, e);
        }
    }

    protected void setAttributes(E element, ConfigNode node, Map<String, String> expressionAtts) {
        Map<String, Attribute> attributes = TagDef.getDefinition(node.getName());

        if (attributes == null) {
            throw new ConfigurationException(error("Invalid tagName found: " + node.getName()));
        }
        for (Map.Entry<String, Attribute> entry : attributes.entrySet()) {
            String attName = entry.getKey();
            Attribute attribute = entry.getValue();
            String attValue = node.getAttribute(attName);
            try {
                if (isExpression(attValue) && !AttributeDef.VALUE.equals(attribute)) {
                    // add expressions to map to resolve then through component proxy
                    expressionAtts.put("get" + attribute.name.toLowerCase(), attValue);
                    continue;
                }
                if (attribute.assignable) {
                    Object value;
                    String setter = (attribute.setter == null) ? attribute.name : attribute.setter;
                    if (!node.hasAttribute(attName)) {
                        // att hasn't been set
                        value = getDefaultAttributeValue(element, node, attName);
                    } else {
                        if (attribute.resolver == null) {
                            // convert value if needed
                            value = (attribute.type == null) ? attValue :
                                    ConvertUtils.convert(attValue, attribute.type);
                        } else {
                            // use resolver to set attribute
                            AttributeResolver resolver = getAttributeResolver(attribute.resolver);
                            value = resolver.resolve(node, attribute.name);
                        }
                    }
                    if (value != null) {
                        BeanUtils.setProperty(element, setter, value);
                    }
                } else {
                    //TODO: create strategies per attribute?
                    // for now let the builder assume this responsibility and reuse with helpers
                    doWithAttribute(element, attName, attValue);
                }
            } catch (Exception e) {
                throw new ConfigurationException(error(String.format("Error while trying to set " +
                        "attribute '%s' on element <%s/>.", attName, node.getName()), e), e);
            }
        }
    }

    private boolean isExpression(String value) {
        return value.contains("${") || value.contains("#{");
    }

    protected Object getDefaultAttributeValue(E element, ConfigNode node, String attName) {
        return null;
    }

    protected AttributeResolver getAttributeResolver(String resolver) {
        return this.getFactory().getAttributeResolver(resolver);
    }


    /**** internal work methods **/

    public void setFactory(ComponentBuilderFactory factory) {
        this.factory = factory;
    }

    protected ComponentBuilderFactory getFactory() {
        return this.factory;
    }


}
