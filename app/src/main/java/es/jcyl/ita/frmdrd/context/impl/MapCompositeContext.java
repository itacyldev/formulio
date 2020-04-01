package es.jcyl.ita.frmdrd.context.impl;

import java.util.ArrayList;
import java.util.Collection;

import es.jcyl.ita.crtrepo.context.AbstractMapContext;
import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.context.Context;

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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class MapCompositeContext extends AbstractMapContext implements CompositeContext {


    @Override
    public void addContext(Context context) {
        this.put(context.getPrefix(), context);
    }

    @Override
    public void addAllContext(Collection<Context> collection) {
        for (Context ctx : collection) {
            addContext(ctx);
        }
    }

    @Override
    public void removeContext(Context context) {
        this.remove(context.getPrefix());
    }

    @Override
    public void removeContext(String s) {
        this.remove(s);
    }

    @Override
    public void removeAllContexts() {
        this.clear();
    }

    @Override
    public boolean hasContext(String s) {
        return has(s);
    }

    @Override
    public Context getContext(String s) {
        return (Context) this.get(s);
    }

    @Override
    public Collection<Context> getContexts() {
        Collection<Context> l = new ArrayList<>();
        for (String prefix : this.keySet()) {
            l.add(getContext(prefix));
        }
        return l;
    }
}
