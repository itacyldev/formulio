package es.jcyl.ita.formic.forms.utils;
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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.FormConfig;
import es.jcyl.ita.formic.forms.config.builders.ComponentBuilderFactory;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.config.reader.ConfigReadingInfo;
import es.jcyl.ita.formic.forms.config.reader.xml.XmlConfigFileReader;
import es.jcyl.ita.formic.forms.project.Project;

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

    public static String createMainList(String nestedXml) {
        return String.format(BASE, nestedXml);
    }

    public static final String BASE_EDIT = "<main repo=\"contacts\"><edit>%s</edit></main>";

    public static String createMainEdit(String nestedXml) {
        return String.format(BASE_EDIT, nestedXml);
    }

    public static String createEditForm(String nestedXml) {
        return createMainEdit(String.format("<form>%s</form>", nestedXml));
    }

    public static FormConfig readFormConfig(String xml) {
        return readFormConfig(null, xml);
    }

    public static FormConfig readFormConfig(File f) {
        String xml = null;
        try {
            xml = FileUtils.readFileToString(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return readFormConfig(null, xml);
    }

    public static FormConfig readFormConfig(Project p, String xml) {
        DevConsole.clear();
        ConfigReadingInfo readingInfo = new ConfigReadingInfo();
        readingInfo.setProject(p);
        DevConsole.setConfigReadingInfo(readingInfo);
        // set current shared into with builder factory
        ComponentBuilderFactory.getInstance().setInfo(readingInfo);

        XmlConfigFileReader reader = new XmlConfigFileReader();
        reader.setListener(readingInfo);
        InputStream is = XmlConfigUtils.createStream(xml);
        ConfigNode root = reader.read(is);
        return (FormConfig) root.getElement();
    }

}
