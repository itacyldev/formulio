package es.jcyl.ita.formic.jayjobs.utils;
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

import java.io.File;
import java.net.URL;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.core.context.impl.OrderedCompositeContext;
import es.jcyl.ita.formic.core.context.impl.UnPrefixedCompositeContext;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ContextTestUtils {

    public static File getBuildDir(){
        ClassLoader classLoader = ContextTestUtils.class.getClassLoader();
        URL resource = classLoader.getResource(".");
        if (resource == null) {
            throw new RuntimeException(String.format("Couldn't find build folder, make sure the file is included" +
                    " in the test-resource folder, or in the device's sdcard."));
        }
        return new File(resource.getFile());
    }

    public static CompositeContext createGlobalContext() {
        CompositeContext ctx = new UnPrefixedCompositeContext();
        Context appContext = new BasicContext("app");
        // set build folder as working file
        appContext.put("workingFolder", ContextTestUtils.getBuildDir().getAbsolutePath());
        ctx.addContext(appContext);
        return ctx;
    }

    public static CompositeContext createGlobalContextWithParam(String param, Object value) {
        return createGlobalContextWithParam(new String[]{param}, new Object[]{value});
    }

    public static CompositeContext createGlobalContextWithParam(String[] params, Object[] values) {
        CompositeContext ctx = new OrderedCompositeContext();
        BasicContext bc = new BasicContext("params");
        for (int i = 0; i < params.length; i++) {
            bc.put(params[i], values[i]);
        }
        ctx.addContext(bc);
        return ctx;
    }
}
