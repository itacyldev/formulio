package es.jcyl.ita.frmdrd.processors;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlScript;

import es.jcyl.ita.crtrepo.context.AbstractMapContext;
import es.jcyl.ita.crtrepo.context.Context;

/*
 * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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

public class JexlProcessor implements Processor {

    @Override
    public Object evaluate(String scriptText, String filename, Context context) {

        JexlBuilder builder = new JexlBuilder();
        JexlEngine jexl = builder.cache(512).strict(true).silent(false).create();

        JexlScript e = jexl.createScript(scriptText);

        Object result = e.execute((AbstractMapContext) context);

        return result;
    }
}
