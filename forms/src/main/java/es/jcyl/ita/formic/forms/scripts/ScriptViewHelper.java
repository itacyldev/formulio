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

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.context.impl.ViewContext;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.IWidget;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.forms.view.widget.StatefulWidget;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;
import es.jcyl.ita.formic.repo.Entity;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 * Javascript helper to provide shortcuts to avoid boilerplate code
 */
public class ScriptViewHelper {
    ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();

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
     *
     * @return
     */
    public ScriptableList<WidgetContextHolder> viewHolders() {
        ScriptableList<WidgetContextHolder> lst = new ScriptableList<>(this.env.getScriptEngine());
        for (WidgetContextHolder holder : env.getRootWidget().getContextHolders()) {
            lst.add(holder);
        }
        return lst;
    }

    /**
     * Returns all the ViewContexts in current View
     *
     * @return
     */
    public ScriptableList<ViewContext> viewContexts() {
        ScriptableList<ViewContext> lst = new ScriptableList<>(this.env.getScriptEngine());
        for (WidgetContextHolder holder : env.getRootWidget().getContextHolders()) {
            lst.add(holder.getWidgetContext().getViewContext());
        }
        return lst;
    }

    /**
     * Returns all the StatefullWidgets in current View
     *
     * @return
     */
    public ScriptableList<StatefulWidget> widgets() {
        ScriptableList<StatefulWidget> lst = new ScriptableList<>(this.env.getScriptEngine());
        for (WidgetContextHolder holder : env.getRootWidget().getContextHolders()) {
            lst.addAll(holder.getWidgetContext().getViewContext().getStatefulWidgets());
        }
        return lst;
    }

    public IWidget widget(String componentId) {
        IWidget widget = null;
        ScriptableList<StatefulWidget> lst = new ScriptableList<>(this.env.getScriptEngine());
        for (WidgetContextHolder holder : env.getRootWidget().getContextHolders()) {
            widget = holder.getWidgetContext().getViewContext().findWidget(componentId);
            if (widget != null) {
                break;
            }
        }
        return widget;
    }

    /**
     * Returns all the ViewContexts in current View
     *
     * @return
     */
    public ScriptableList<Entity> entities() {
        ScriptableList<Entity> lst = new ScriptableList<>(this.env.getScriptEngine());
        for (WidgetContextHolder holder : env.getRootWidget().getContextHolders()) {
            lst.add(holder.getWidgetContext().getEntity());
        }
        return lst;
    }

    public void setWidgetValue(String componentId, Object value) {
        IWidget widget = widget(componentId);
        if (!(widget instanceof InputWidget)) {
            error("Cannot set value on component %s, only InputWidget can have setValue() method.");
            return;
        }
        ((InputWidget)widget).setValue(value);
    }

    public void setUIValue(UIComponent component, Object value) {
        if (value != null) {
            component.setValueExpression(exprFactory.getInstance().create(value.toString()));
        } else {
            component.setValueExpression(null);
        }
    }
}
