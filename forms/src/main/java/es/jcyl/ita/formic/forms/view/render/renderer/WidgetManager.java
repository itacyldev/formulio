package es.jcyl.ita.formic.forms.view.render.renderer;
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

import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.forms.view.widget.ControllableWidget;
import es.jcyl.ita.formic.forms.view.widget.StatefulWidget;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.forms.view.widget.WidgetContext;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;

/**
 * Register the widgets created in current view to make sure android ui objects are disposed when the view is deleted
 *
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class WidgetManager {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    WidgetContextDisposer disposer = new WidgetContextDisposer();

    private WeakHashMap<String, WidgetContext> wContexts = new WeakHashMap<>();

    public WidgetContext registerWidget(RenderingEnv env, Widget widget) {
        WidgetContext currentWCtx;
        if (widget instanceof WidgetContextHolder) {
            // create widgetContext and set to current widget
            currentWCtx = new WidgetContext((WidgetContextHolder) widget);
            // set the entity used to render this widget in its context
            currentWCtx.setEntity(env.getEntity());
            // set message context
            BasicContext msgCtx = env.getMessageContext(widget.getComponentId());
            currentWCtx.setMessageContext(msgCtx);
            // add global context
            currentWCtx.addContext(env.getGlobalContext());
            widget.setWidgetContext(currentWCtx);
            // set as current WidgetContext so nested elements will use it
            env.setWidgetContext(currentWCtx);
            // register current widget in view
            if (env.getRootWidget() != null) {
                env.getRootWidget().registerContextHolder((WidgetContextHolder) widget);
            }
            // add to active widgetContext
            this.wContexts.put(currentWCtx.getHolderId(), currentWCtx);
        } else {
            widget.setWidgetContext(env.getWidgetContext());
            if (env.getWidgetContext() != null) {
                env.getWidgetContext().registerWidget(widget);
            }
            currentWCtx = widget.getWidgetContext();
        }
        widget.setRootWidget(env.getRootWidget());
        if ((widget instanceof ControllableWidget) && (env.getRootWidget() != null)) { // rootWidget == null just in tests
            env.getRootWidget().registerControllableWidget((ControllableWidget) widget);
        }
        return currentWCtx;
    }

    public void dispose() {
        disposer.setContexts(wContexts);
        executor.execute(disposer);
    }


    public class WidgetContextDisposer implements Runnable {
        private WeakHashMap<String, WidgetContext> wContexts;

        @Override
        public void run() {
            for (WidgetContext wc : wContexts.values()) {
                List<StatefulWidget> widgetList = wc.getStatefulWidgets();
                if (widgetList != null) {
                    for (StatefulWidget widget : widgetList) {
                        widget.dispose();
                    }
                }
                wc.clear();
            }
        }

        public void setContexts(WeakHashMap<String, WidgetContext> wContexts) {
            this.wContexts = new WeakHashMap<>(wContexts);
        }
    }
}
