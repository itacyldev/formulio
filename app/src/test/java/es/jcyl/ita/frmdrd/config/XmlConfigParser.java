package es.jcyl.ita.frmdrd.config;
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

import android.util.AttributeSet;
import android.util.Xml;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import es.jcyl.ita.crtrepo.test.utils.TestUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class XmlConfigParser {

    @BeforeClass
    public static void setUp() {
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    @Test
    public void testBasic() throws Exception {
        File file = TestUtils.findFile("config/test1.xml");
        parseXML(file);

    }

    private void parseXML(File file) throws Exception {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        InputStream is = new FileInputStream(file);

        factory.setNamespaceAware(false);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput(is, null);

        processParsing(xpp);
    }

    private void processParsing(XmlPullParser xpp) throws IOException, XmlPullParserException {

        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
                System.out.println("Start document");
            } else if (eventType == XmlPullParser.START_TAG) {
                printAttSet(xpp);
                System.out.println("Start tag " + xpp.getName());
            } else if (eventType == XmlPullParser.END_TAG) {
                System.out.println("End tag " + xpp.getName());
            } else if (eventType == XmlPullParser.TEXT) {
                System.out.println("Text " + xpp.getText());
            }
            eventType = xpp.next();
        }

    }

    private void printAttSet(XmlPullParser xpp) {
        for (int i = 0; i < xpp.getAttributeCount(); i++) {
            System.out.println(String.format("%s - %s", xpp.getAttributeName(i), xpp.getAttributeValue(i)));
        }
    }


}
