package es.jcyl.ita.formic.forms.scripts;
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

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.context.impl.ViewContext;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 * Javascript helper to provide shortcuts to avoid boilerplate code
 */
public class ScriptViewHelper {

    private final CompositeContext ctx;
    private final RenderingEnv env;

    public ScriptViewHelper(RenderingEnv env, CompositeContext ctx) {
        this.ctx = ctx;
        this.env = env;
    }

    /**
     * Retrieves the ViewContext link to the the given ViewContextHolder
     *
     * @param id
     * @return
     */
    public ViewContext viewContext(String id) {
        WidgetContextHolder ctxHolder = env.getRootWidget().getContextHoldersMap().get(id);
        if (ctxHolder == null) {
            return null;
        }
        return ctxHolder.getWidgetContext().getViewContext();
    }

    /**
     * Returns all the ViewContextHolders in current View
     * @return
     */
    public ScriptableList<WidgetContextHolder> viewHolders() {
        ScriptableList<WidgetContextHolder> lst = new ScriptableList<>(this.env.getScriptEngine());
        for(WidgetContextHolder holder: env.getRootWidget().getContextHolders()){
            lst.add(holder);
        }
        return lst;
    }

    /**
     * Returns all the ViewContexts in current View
     * @return
     */
    public ScriptableList<ViewContext> viewContexts() {
        ScriptableList<ViewContext> lst = new ScriptableList<>(this.env.getScriptEngine());
        for(WidgetContextHolder holder: env.getRootWidget().getContextHolders()){
            lst.add(holder.getWidgetContext().getViewContext());
        }
        return lst;
    }
}
