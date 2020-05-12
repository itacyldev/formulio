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
import org.mini2Dx.beanutils.ConvertUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.jcyl.ita.frmdrd.config.AttributeResolver;
import es.jcyl.ita.frmdrd.config.ComponentBuilder;
import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.meta.Attribute;
import es.jcyl.ita.frmdrd.config.meta.TagDef;
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
        this.attributeDefs = TagDef.getDefinition(tagName);
        this.elementType = clazz;
    }

    @Override
    public E build(ConfigNode<E> node) {
        E element = instantiate();
        setAttributes(element, node);
//        setId(element);
        node.setElement(element);
        doConfigure(element, node);
        return element;
    }


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

        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            String attName = entry.getKey();
            try {
                Attribute attribute = this.attributeDefs.get(attName);
                if (attribute == null) {
                    error(String.format("Invalid attribute found in tag <${tag}/>: [%s].", attName));
                } else if (attribute.assignable) {
                    String setter = (attribute.setter == null) ? attName : attribute.setter;
                    Object value;
                    if (attribute.resolver == null) {
                        // convert value if needed
                        value = (attribute.type == null) ? entry.getValue() :
                                ConvertUtils.convert(entry.getValue(), attribute.type);
                    } else {
                        // use resolver to set attribute
                        AttributeResolver resolver = getAttributeResolver(attribute.resolver);
                        value = resolver.resolve(node, attribute.name);
                    }
                    // set attribute using reflection
                    BeanUtils.setProperty(element, setter, value);
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

    protected AttributeResolver getAttributeResolver(String resolver) {
        return this.getFactory().getAttributeResolver(resolver);
    }

    abstract protected void doWithAttribute(E element, String name, String value);

    protected abstract void doConfigure(E element, ConfigNode<E> node);


    /******* Helper methods ********/


    /**** internal work methods **/

    public void setFactory(ComponentBuilderFactory factory) {
        this.factory = factory;
    }

    protected ComponentBuilderFactory getFactory() {
        return this.factory;
    }


}
