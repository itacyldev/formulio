package es.jcyl.ita.formic.forms.config.reader.xml;
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
import org.mini2Dx.collections.CollectionUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.builders.ComponentBuilder;
import es.jcyl.ita.formic.forms.config.builders.ComponentBuilderFactory;
import es.jcyl.ita.formic.forms.config.meta.Attribute;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.config.reader.ReadingProcessListener;
import es.jcyl.ita.formic.forms.config.resolvers.ComponentResolver;

/**
 * Reads form configuration files and creates form controllers and view Components.
 * It first goes over the tree creating a simple ConfigNode and check the element ids, and after
 * that uses builders to create view components and controllers.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class XmlConfigFileReader {
    private static final String RELAXED_FEATURE = "http://xmlpull.org/v1/doc/features.html#relaxed";
    private List<ReadingProcessListener> listeners = new ArrayList<>();

    private XmlPullParserFactory factory;
    private ComponentBuilderFactory builderFactory = ComponentBuilderFactory.getInstance();
    private ComponentResolver resolver;
    private String currentFile;

    public XmlConfigFileReader() {
        try {
            this.factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            resolver = new ComponentResolver();
            builderFactory.setComponentResolver(resolver);
            List<ReadingProcessListener> listeners = builderFactory.getListeners();
            addListeners(listeners);
        } catch (XmlPullParserException e) {
            throw new ConfigurationException(DevConsole.error("Error occurred while trying to instantiate XMLFileFormConfigReader.", e));
        }
    }

    public ConfigNode read(Uri uri) throws ConfigurationException {
        try {
            // works just for file schemes!!!!
            notifyFileStart(uri.getPath());
            this.currentFile = uri.getPath();
            ConfigNode node = read(new FileInputStream(currentFile));
            notifyFileEnd(currentFile);
            return node;
        } catch (FileNotFoundException e) {
            throw new ConfigurationException("Error while opening config file " + uri.toString(), e);
        }
    }


    private ConfigNode read(InputStream is) throws ConfigurationException {
        XmlPullParser xpp = null;
        try {
            xpp = factory.newPullParser();
            xpp.setInput(is, null);
        } catch (XmlPullParserException e) {
            throw new ConfigurationException(DevConsole.error("Error while trying to create XmlPullParser", e), e);
        }
        try {
            ConfigNode rootNode = readFile(xpp);
            if (rootNode != null) { // file is not empty
                build(rootNode);
            }
            return rootNode;
        } catch (Exception e) {
            throw new ConfigurationException(DevConsole.error("Error while trying to read configuration file '${file}'.", e), e);
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
                // pass
            } else if (eventType == XmlPullParser.START_TAG) {
                // get builder for this tag
                currentNode = new ConfigNode(xpp.getName());
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


    private void setIdIfNull(ConfigNode node, ComponentResolver resolver) {
        if (StringUtils.isBlank(node.getId())) {
            // number elements with of current tag
            String tag = node.getName();
            String id = null;
            if (tag.toLowerCase().equals("main")) {
                // use filename as id
                id = FilenameUtils.getBaseName(this.currentFile);
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
        notifyElementStart(root);

        DevConsole.debug("Starting tag: ${tag}");
        if (builder != null) {
            Object element = builder.build(root);
            DevConsole.debug("<${tag}/> element created.");
            root.setElement(element);
        }
        List<ConfigNode> children = root.getChildren();
        for (ConfigNode kid : children) {
            notifyElementStart(kid);
            build(kid);
            notifyElementEnd(kid);
        }
        if (builder != null) {
            DevConsole.debug("Processing children of <${tag}/>");
            builder.processChildren(root);
        }
        notifyElementEnd(root);
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


    public void addListener(ReadingProcessListener listener) {
        if (listener != null) {
            this.listeners.add(listener);
        }
    }

    public void addListeners(List<ReadingProcessListener> listeners) {
        if (listeners != null) {
            for (ReadingProcessListener listener : listeners) {
                addListener(listener);
            }
        }
    }

    private void notifyFileStart(String path) {
        if (CollectionUtils.isNotEmpty(listeners)) {
            for (ReadingProcessListener listener : listeners) {
                listener.fileStart(path);
            }
        }
    }

    private void notifyFileEnd(String path) {
        if (CollectionUtils.isNotEmpty(listeners)) {
            for (ReadingProcessListener listener : listeners) {
                listener.fileEnd(path);
            }
        }
    }

    private void notifyElementStart(ConfigNode node) {
        if (CollectionUtils.isEmpty(listeners)) {
            return;
        }
        boolean isView = false;
        String tagName = node.getName().toLowerCase();
        if (tagName.equals("edit") || tagName.equals("list")) {
            isView = true;
        }
        for (ReadingProcessListener listener : listeners) {
            if (isView) {
                listener.viewStart(node);
            }
            listener.elementStart(node);
        }
    }

    private void notifyElementEnd(ConfigNode node) {
        if (CollectionUtils.isEmpty(listeners)) {
            return;
        }
        boolean isView = false;
        String tagName = node.getName().toLowerCase();
        if (tagName.equals("edit") || tagName.equals("list")) {
            isView = true;
        }
        for (ReadingProcessListener listener : listeners) {
            listener.elementEnd(node);
            if (isView) {
                listener.viewEnd(node);
            }
        }
    }
}
