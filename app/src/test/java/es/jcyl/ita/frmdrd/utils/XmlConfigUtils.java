package es.jcyl.ita.frmdrd.utils;
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

import org.junit.Assert;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import es.jcyl.ita.frmdrd.config.FormConfig;
import es.jcyl.ita.frmdrd.config.reader.XMLFileFormConfigReader;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.UIComponentHelper;
import es.jcyl.ita.frmdrd.ui.components.datatable.UIDatatable;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class XmlConfigUtils {

    public static InputStream createStream(String string) {
        File file = null;
        try {
            file = File.createTempFile("xml-test", ".xml");
            file.deleteOnExit();
            PrintWriter out = new PrintWriter(file);
            out.write(string);
            out.close();
            return new FileInputStream(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final String BASE = "<main repo=\"contacts\"><list>%s</list></main>";
    public static String createMainList(String nestedXml){
        return String.format(BASE, nestedXml);
    }

    public static FormConfig readFormConfig(String xml) throws XmlPullParserException {
        XMLFileFormConfigReader reader = new XMLFileFormConfigReader();
        InputStream is = XmlConfigUtils.createStream(xml);
        return reader.read("test1", is);
    }

}
