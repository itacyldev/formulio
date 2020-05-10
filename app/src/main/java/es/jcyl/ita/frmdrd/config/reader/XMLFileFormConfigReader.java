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

import android.net.Uri;

import org.apache.commons.lang3.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import es.jcyl.ita.frmdrd.config.ComponentBuilder;
import es.jcyl.ita.frmdrd.config.ComponentBuilderFactory;
import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.DevConsole;
import es.jcyl.ita.frmdrd.config.FormConfig;
import es.jcyl.ita.frmdrd.config.meta.Attribute;
import es.jcyl.ita.frmdrd.config.meta.Attributes;
import es.jcyl.ita.frmdrd.config.resolvers.ComponentResolver;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Reads form configuration files and creates form controllers and view Components.
 * It first goes over the tree creating a simple ConfigNode and check the element ids, and after
 * that uses builders to create view components and controllers.
 */

public class XMLFileFormConfigReader extends AbstractFormConfigReader {

    private XmlPullParserFactory factory;
    private ComponentBuilderFactory builderFactory = ComponentBuilderFactory.getInstance();
    private ComponentResolver resolver;

    public XMLFileFormConfigReader() throws XmlPullParserException {
        this.factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        resolver = new ComponentResolver();
        builderFactory.setComponentResolver(resolver);
    }


    @Override
    public FormConfig read(String name, Uri uri) throws ConfigurationException {
        try {
            // works just for file schemes!!!!
            return read(name, new FileInputStream(uri.getPath()));
        } catch (FileNotFoundException e) {
            throw new ConfigurationException("Error while opening config file " + uri.toString(), e);
        }
    }

    public FormConfig read(String name, InputStream is) throws ConfigurationException {
        XmlPullParser xpp = null;
        try {
            xpp = factory.newPullParser();
            xpp.setInput(is, null);
        } catch (XmlPullParserException e) {
            throw new ConfigurationException(error("Error while trying to create XmlPullParser", e), e);
        }
        DevConsole.setCurrentFile(name);
        DevConsole.setParser(xpp);

        FormConfig config = null;
        try {
            ConfigNode rootNode = readFile(xpp);
            build(rootNode);
            config = (FormConfig) rootNode.getElement();
        } catch (Exception e) {
            throw new ConfigurationException(error("Error while trying to read configuration file ${file}.", e), e);
        }

        checkName(config, name);
        if (DevConsole.hasCurrentFileError()) {
            throw new ConfigurationException("An error occurred during configuration reading, check developer console.");
        }
        return config;
    }

    /**
     * If name attribute has not been set, use the file name as default value
     *
     * @param config
     */
    private void checkName(FormConfig config, String fileName) {
        // get default name, in case it hasn't been set in the "form" tag
        if (StringUtils.isBlank(config.getName())) {
            String defName = fileName.substring(0, fileName.lastIndexOf("."));
            config.setName(defName);
        }
    }

    public ConfigNode readFile(XmlPullParser xpp) throws IOException, XmlPullParserException {
        Stack<ConfigNode> nodeStack = new Stack<>();
        // not thread-safe
        resolver.clear();
        ConfigNode parentNode, currentNode = null;

        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_DOCUMENT) {

            } else if (eventType == XmlPullParser.START_TAG) {
                // get builder for this tag
                currentNode = new ConfigNode(xpp.getName());
                DevConsole.setCurrentElement(xpp.getName());

                setAttributes(xpp, currentNode, resolver);
                setIdIfNull(currentNode, resolver);
                // store current node in the pile
                nodeStack.push(currentNode);

            } else if (eventType == XmlPullParser.TEXT) {
                // add text to current builder
                currentNode.addText(xpp.getText());
            } else if (eventType == XmlPullParser.END_TAG) {
                // remove current node from the stack
                currentNode = nodeStack.pop();

                System.out.println("Finishing with node: " + currentNode.getName());
                if (!nodeStack.empty()) {
                    // get parent builder from the pile and add current node
                    parentNode = nodeStack.peek();
                    parentNode.addChild(currentNode);
                }
            }
            eventType = xpp.next();
        }
        return currentNode;
    }

    private void setIdIfNull(ConfigNode node, ComponentResolver resolver) {
        if (StringUtils.isBlank(node.getId())) {
            // number elements with of current tag
            String tag = node.getName();
            Set<String> tags = this.resolver.getIdsForTag(tag);
            node.setId(tag + tags.size() + 1); // table1, table2, table3,..
        }
    }

    private void build(ConfigNode root) {
        ComponentBuilder builder = builderFactory.getBuilder(root.getName());
        if (builder != null) {
            Object element = builder.build(root);
            root.setElement(element);
        }
        List<ConfigNode> children = root.getChildren();
        for (ConfigNode kid : children) {
            build(kid);
        }
        if (builder != null) {
            builder.processChildren(root);
        }
    }

//
//    private FormConfig build(ConfigNode root)  {
//
//        // create repositories if needed
//
//        ComponentBuilder currentBuilder = null, parentBuilder = null;
//
//        Stack<ComponentBuilder> builderStack = new Stack<>();
//
//        String currentTag = null;
//
//        // not thread-safe
//        resolver.clear();
//
//        int eventType = xpp.getEventType();
//        while (eventType != XmlPullParser.END_DOCUMENT) {
//
//            if (eventType == XmlPullParser.START_DOCUMENT) {
//
//            } else if (eventType == XmlPullParser.START_TAG) {
//                // get builder for this tag
//                currentTag = xpp.getName();
//                DevConsole.setCurrentElement(currentTag);
//                currentBuilder = builderFactory.getBuilder(currentTag);
//                if (currentBuilder == null) {
//                    throw new ConfigurationException(error("No builder found for tag ${tag} " +
//                            "in file ${file}, current element and nested ones will be ignored."));
//                }
////                setAttributes(xpp, currentBuilder, resolver);
//                // store current builder in the pile
//                builderStack.push(currentBuilder);
//
//            } else if (eventType == XmlPullParser.TEXT) {
//                // add text to current builder
//                currentBuilder.addText(xpp.getText());
//            } else if (eventType == XmlPullParser.END_TAG) {
//                // tell the builder to create element and append to parent builder
//
//                currentBuilder = builderStack.pop();
//                ConfigNode component = currentBuilder.build();
//                System.out.println("Building component: " + component.getName());
//                if (!builderStack.empty()) {
//                    // get parent builder from the pile
//                    parentBuilder = builderStack.peek();
//                    parentBuilder.addChild(currentTag, component);
//                    currentBuilder = parentBuilder;
//                }
//            }
//            eventType = xpp.next();
//        }
//        return currentBuilder.build();
//    }

    private void setAttributes(XmlPullParser xpp, ConfigNode node, ComponentResolver idResolver) {
        Map<String, Attribute> attributesDef = Attributes.getDefinition(node.getName());

        for (int i = 0; i < xpp.getAttributeCount(); i++) {
            String attName = xpp.getAttributeName(i);
            String value = xpp.getAttributeValue(i);
            if ("id".equals(attName.toLowerCase())) {
                // register current component id
                idResolver.addComponentId(value, xpp.getName());
            }
            if (!isAttributeSupported(attributesDef, attName)) {
                error(String.format("Unsupported attribute: '%s' in element <${tag}/> in file '${file}'.", attName));
            } else {
                node.getAttributes().put(attName, value);
            }
        }
    }

    protected boolean isAttributeSupported(Map<String, Attribute> attributes, String attName) {
        return (attributes == null) ? false : attributes.containsKey(attName);
    }

}
