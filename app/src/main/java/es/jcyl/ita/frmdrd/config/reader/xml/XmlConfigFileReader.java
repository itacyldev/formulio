package es.jcyl.ita.frmdrd.config.reader.xml;
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

import org.apache.commons.io.FilenameUtils;
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
import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.DevConsole;
import es.jcyl.ita.frmdrd.config.builders.ComponentBuilderFactory;
import es.jcyl.ita.frmdrd.config.meta.Attribute;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.config.reader.ReadingProcessListener;
import es.jcyl.ita.frmdrd.config.resolvers.ComponentResolver;

import static es.jcyl.ita.frmdrd.config.DevConsole.debug;
import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Reads form configuration files and creates form controllers and view Components.
 * It first goes over the tree creating a simple ConfigNode and check the element ids, and after
 * that uses builders to create view components and controllers.
 */

public class XmlConfigFileReader {
    private ReadingProcessListener listener;

    private XmlPullParserFactory factory;
    private ComponentBuilderFactory builderFactory = ComponentBuilderFactory.getInstance();
    private ComponentResolver resolver;

    public XmlConfigFileReader() {
        try {
            this.factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            resolver = new ComponentResolver();
            builderFactory.setComponentResolver(resolver);
        } catch (XmlPullParserException e) {
            throw new ConfigurationException(error("Error occurred while trying to instantiate XMLFileFormConfigReader.", e));
        }
    }

    public ConfigNode read(Uri uri) throws ConfigurationException {
        try {
            // works just for file schemes!!!!
            return read(new FileInputStream(uri.getPath()));
        } catch (FileNotFoundException e) {
            throw new ConfigurationException("Error while opening config file " + uri.toString(), e);
        }
    }

    public ConfigNode read(InputStream is) throws ConfigurationException {
        XmlPullParser xpp = null;
        try {
            xpp = factory.newPullParser();
            xpp.setInput(is, null);
        } catch (XmlPullParserException e) {
            throw new ConfigurationException(error("Error while trying to create XmlPullParser", e), e);
        }
        try {
            ConfigNode rootNode = readFile(xpp);
            if (rootNode != null) { // file is not empty
                build(rootNode);
            }
            return rootNode;
        } catch (Exception e) {
            throw new ConfigurationException(error("Error while trying to read configuration file '${file}'.", e), e);
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
                notifyNewElement(xpp.getName());

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

    private void notifyNewElement(String name) {
        if (listener != null) {
            listener.newElement(name);
        }
    }

    private void setIdIfNull(ConfigNode node, ComponentResolver resolver) {
        if (StringUtils.isBlank(node.getId())) {
            // number elements with of current tag
            String tag = node.getName();
            String id = null;
            if (tag.toLowerCase().equals("main")) {
                // use filename as id
                String currentFile = listener.getCurrentFile();
                id = FilenameUtils.getBaseName(currentFile);
            } else {
                Set<String> tags = this.resolver.getIdsForTag(tag);
                id = tag + (tags.size() + 1);  // table1, table2, table3,..
            }
            node.setId(id);
        }
        this.resolver.addComponentId(node.getName(), node.getId());
    }

    public void build(ConfigNode root) {
        ComponentBuilder builder = builderFactory.getBuilder(root.getName());
        notifyNewElement(root.getName());

        debug("Starting tag: ${tag}");

        if (builder != null) {
            Object element = builder.build(root);
            debug("<${tag}/> element created.");
            root.setElement(element);
        }
        List<ConfigNode> children = root.getChildren();
        for (ConfigNode kid : children) {
            build(kid);
        }
        if (builder != null) {
            debug("Processing children of <${tag}/>");
            builder.processChildren(root);
        }
        DevConsole.debug(root);
        DevConsole.debug("Ending tag: ${tag}");
    }


    private void setAttributes(XmlPullParser xpp, ConfigNode node, ComponentResolver idResolver) {
        for (int i = 0; i < xpp.getAttributeCount(); i++) {
            String attName = xpp.getAttributeName(i);
            String value = xpp.getAttributeValue(i);
            if ("id".equals(attName.toLowerCase())) {
                // register current component id
                idResolver.addComponentId(value, xpp.getName());
            }
            node.getAttributes().put(attName, value);
        }
    }

    protected boolean isAttributeSupported(Map<String, Attribute> attributes, String attName) {
        return (attributes == null) ? false : attributes.containsKey(attName);
    }


    public void setListener(ReadingProcessListener listener) {
        this.listener = listener;
    }

}
