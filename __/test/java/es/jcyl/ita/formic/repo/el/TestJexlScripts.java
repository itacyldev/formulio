package es.jcyl.ita.formic.repo.el;
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

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlScript;
import org.apache.commons.jexl3.JxltEngine;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.internal.Engine;
import org.apache.commons.jexl3.internal.TemplateEngine;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import es.jcyl.ita.crtrepo.test.utils.TestUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class TestJexlScripts {
    protected static final JexlEngine jexl = new JexlBuilder().cache(256)
            .strict(true).silent(false).create();

    protected static final JxltEngine jxltEngine = new TemplateEngine((Engine) jexl, false,
            256, '$', '#');


    @Test
    public void testCreateScriptFromFile() throws Exception {
        File file = TestUtils.findFile("scripts/script1.js");
        JexlScript script = jxltEngine.getEngine().createScript(file);

        String source = readSource(file);

        Map<String, Object> ctx = new HashMap<>();
        ctx.put("var1",22);
        ctx.put("var2",33);

        JexlContext jcontext = new MapContext();
        jcontext.set("log", System.out);

        jcontext.set("ctx",ctx);
        Object value = script.execute(jcontext);

        System.out.println(value);
        System.out.println(value.getClass());

        Callable<Object> callable = script.callable(jcontext);
        callable.call();
    }

    protected String readSource(final File file) throws IOException {

        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        return toString(reader);
    }

    protected static String toString(BufferedReader reader) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append('\n');
        }
        return buffer.toString();
    }
}
