package es.jcyl.ita.frmdrd.context.wrappers;
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

import org.apache.commons.jexl3.JexlContext;

import es.jcyl.ita.crtrepo.context.Context;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class JexlContextWrapper implements JexlContext {
    Context context;

    public JexlContextWrapper(Context context) {
        this.context = context;
    }

    @Override
    public Object get(String name) {
        return context.get(name);
    }

    @Override
    public void set(String name, Object value) {
        context.put(name, value);

    }

    @Override
    public boolean has(String name) {
        return context.containsKey(name);
    }
}
