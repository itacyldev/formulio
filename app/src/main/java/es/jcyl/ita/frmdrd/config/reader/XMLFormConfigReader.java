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

import es.jcyl.ita.frmdrd.config.ConfigConsole;
import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.FormConfig;
import es.jcyl.ita.frmdrd.config.builders.ComponentBuilder;
import es.jcyl.ita.frmdrd.config.builders.ComponentBuilderFactory;
import es.jcyl.ita.frmdrd.config.builders.FormControllerBuilder;

import static es.jcyl.ita.frmdrd.config.ConfigConsole.error;
import static es.jcyl.ita.frmdrd.config.ConfigConsole.warn;

/**
 * Reads form configuration files and creates form controllers and view Components
 */
public class XMLFormConfigReader {

    XmlPullParserFactory factory;
    ComponentBuilderFactory builderFactory = ComponentBuilderFactory.getInstance();

    public XMLFormConfigReader() throws XmlPullParserException {
        this.factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
    }


    public FormConfig read(File file) throws Exception {
        InputStream is = new FileInputStream(file);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(is, null);

        ConfigConsole.setCurrentFile(file.getAbsolutePath());
        BaseConfigNode root = processFile(xpp);

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

    private BaseConfigNode processFile(XmlPullParser xpp) throws IOException, XmlPullParserException {
        ComponentBuilder currentBuilder = null, parentBuilder = null;

        Stack<ComponentBuilder> builderStack = new Stack<>();

        String currentTag = null;


        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {

            } else if (eventType == XmlPullParser.START_TAG) {
                // store current builder in the pile
                if (currentBuilder != null) {
                    builderStack.push(currentBuilder);
                }
                // get builder for this tag
                currentTag = xpp.getName();
                currentBuilder = builderFactory.getBuilder(currentTag);
                if (currentBuilder == null) {
                    String msg =String.format("No builder found for tag [%s], current element and nested ones are ignored.", currentTag);
                    error(msg);
                    throw new ConfigurationException(msg);
                } else {
                    currentBuilder.setParser(xpp);
                    setAttributes(xpp, currentBuilder);
                }
            } else if (eventType == XmlPullParser.TEXT) {
                // add text to current builder
                currentBuilder.addText(xpp.getText());

            } else if (eventType == XmlPullParser.END_TAG) {
                // tell the builder to create element and append to parent builder
                BaseConfigNode component = currentBuilder.build();
                if (!builderStack.empty()) {
                    // restore parent builder from the pile
                    parentBuilder = builderStack.pop();
                    parentBuilder.addChild(currentTag, component);
                    currentBuilder = parentBuilder;
                }
            }
            eventType = xpp.next();
        }
        return currentBuilder.build();
    }

    private void setAttributes(XmlPullParser xpp, ComponentBuilder builder) {
        for (int i = 0; i < xpp.getAttributeCount(); i++) {
            builder.withAttribute(xpp.getAttributeName(i), xpp.getAttributeValue(i));
        }
    }
}
