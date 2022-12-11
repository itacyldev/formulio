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
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import es.jcyl.ita.formic.forms.config.meta.Attribute;
import es.jcyl.ita.formic.forms.config.meta.TagDef;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class XSDGenerator {

    @Test
    public void generateXSD() throws IOException {
        StringBuilder stb = new StringBuilder();
        stb.append("<root>");
        Map<String, Map<String, Attribute>> definitions = TagDef.getDefinitions();

        Set<String> orderedTags = new TreeSet<>(definitions.keySet());

        for (String tagName: orderedTags) {
            stb.append("<" + tagName.toLowerCase());
            Map<String, Attribute> atts = definitions.get(tagName);
            for (Attribute att : atts.values()) {
                String value = "";
                if (att.name.equalsIgnoreCase("id")) {
                    value = RandomStringUtils.randomAlphabetic(8);
                } else if (att.name.equalsIgnoreCase("readonly")) {
                    value = (RandomUtils.nextInt(0,11)>=5)?"true":"false";
                }
                stb.append(String.format(" %s=\"%s\"", att.name, value));
            }
            stb.append("/>");
        }
        stb.append("</root>");
        File f = new File("root.xml");
        FileUtils.write(f, stb.toString(), "UTF-8");
        System.out.println("File written: " + f.getAbsolutePath());


    }
}
