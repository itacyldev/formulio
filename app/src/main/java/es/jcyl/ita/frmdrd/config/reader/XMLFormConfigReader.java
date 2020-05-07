package es.jcyl.ita.frmdrd.config.reader;

import org.apache.commons.lang3.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.DevConsole;
import es.jcyl.ita.frmdrd.config.FormConfig;
import es.jcyl.ita.frmdrd.config.builders.ComponentBuilder;
import es.jcyl.ita.frmdrd.config.builders.ComponentBuilderFactory;
import es.jcyl.ita.frmdrd.config.resolvers.ComponentResolver;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * Reads form configuration files and creates form controllers and view Components
 */
public class XMLFormConfigReader {

    XmlPullParserFactory factory;
    ComponentBuilderFactory builderFactory = ComponentBuilderFactory.getInstance();
    ComponentResolver resolver;

    public XMLFormConfigReader() throws XmlPullParserException {
        this.factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        resolver = new ComponentResolver();
        builderFactory.setComponentResolver(resolver);
    }


    public FormConfig read(File file) throws Exception {
        InputStream is = new FileInputStream(file);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(is, null);

        DevConsole.setCurrentFile(file.getAbsolutePath());
        DevConsole.setParser(xpp);

        ConfigNode root = processFile(xpp);

        FormConfig config = (FormConfig) root.getElement();
        checkName(config, file.getName());
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

    private ConfigNode processFile(XmlPullParser xpp) throws IOException, XmlPullParserException {
        ComponentBuilder currentBuilder = null, parentBuilder = null;

        Stack<ComponentBuilder> builderStack = new Stack<>();

        String currentTag = null;

        // not thread-safe
        resolver.clear();

        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_DOCUMENT) {

            } else if (eventType == XmlPullParser.START_TAG) {
                // get builder for this tag
                currentTag = xpp.getName();
                DevConsole.setCurrentElement(currentTag);
                currentBuilder = builderFactory.getBuilder(currentTag);
                if (currentBuilder == null) {
                    throw new ConfigurationException(error("No builder found for tag ${tag} " +
                            "in file ${file}, current element and nested ones will be ignored."));
                }
                setAttributes(xpp, currentBuilder, resolver);
                // store current builder in the pile
                builderStack.push(currentBuilder);

            } else if (eventType == XmlPullParser.TEXT) {
                // add text to current builder
                currentBuilder.addText(xpp.getText());
            } else if (eventType == XmlPullParser.END_TAG) {
                // tell the builder to create element and append to parent builder

                currentBuilder = builderStack.pop();
                ConfigNode component = currentBuilder.build();
                System.out.println("Building component: " + component.getName());
                if (!builderStack.empty()) {
                    // get parent builder from the pile
                    parentBuilder = builderStack.peek();
                    parentBuilder.addChild(currentTag, component);
                    currentBuilder = parentBuilder;
                }
            }
            eventType = xpp.next();
        }
        return currentBuilder.build();
    }

    private void setAttributes(XmlPullParser xpp, ComponentBuilder builder, ComponentResolver idResolver) {
        for (int i = 0; i < xpp.getAttributeCount(); i++) {
            String attName = xpp.getAttributeName(i);
            String value =  xpp.getAttributeValue(i);
            if ("id".equals(attName.toLowerCase())) {
                // register current component id
                idResolver.addComponentId(value, xpp.getName());
            }
            builder.withAttribute(attName,value);
        }
    }
}
