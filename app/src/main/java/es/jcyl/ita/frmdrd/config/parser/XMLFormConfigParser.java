package es.jcyl.ita.frmdrd.config.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import es.jcyl.ita.frmdrd.config.builders.ComponentBuilder;
import es.jcyl.ita.frmdrd.config.builders.ComponentBuilderFactory;

/**
 * Reads form configuration files and creates form controllers and view Components
 */
public class XMLFormConfigParser {
    ComponentBuilderFactory builderFactory = ComponentBuilderFactory.getInstance();

    public void readConfig(List<String> fileList) {

    }


    public void parseFile(File file) throws Exception {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        InputStream is = new FileInputStream(file);

        factory.setNamespaceAware(false);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(is, null);

    }

    private void processParsing(XmlPullParser xpp) throws IOException, XmlPullParserException {
        ComponentBuilder currentBuilder = null, parentBuilder = null;

        String currentTag = null;
        String currentId;
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_DOCUMENT) {
                // read basic elements
            } else if (eventType == XmlPullParser.START_TAG) {
                parentBuilder = currentBuilder;
                // get parsers and builder for this tag
                currentTag = xpp.getName();
                currentBuilder = builderFactory.getBuilder(currentTag);
                readAttributes(xpp, currentBuilder);

            } else if (eventType == XmlPullParser.TEXT) {
                // add text to current builder
                currentBuilder.addText(xpp.getText());

            } else if (eventType == XmlPullParser.END_TAG) {
                // tell the builder to create element and append to parent builder
                ConfigNode component = currentBuilder.build();
                if (parentBuilder != null) {
                    parentBuilder.addChild(currentTag, component);
                }
            }
            eventType = xpp.next();
        }
    }

    private void readAttributes(XmlPullParser xpp, ComponentBuilder builder) {
        for (int i = 0; i < xpp.getAttributeCount(); i++) {
            builder.withAttribute(xpp.getAttributeName(i), xpp.getAttributeValue(i));
        }
    }
}
