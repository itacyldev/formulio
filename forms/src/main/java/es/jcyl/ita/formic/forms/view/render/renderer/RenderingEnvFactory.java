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

import java.util.Map;
import java.util.WeakHashMap;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.scripts.ScriptEngine;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.forms.view.widget.WidgetContext;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;

/**
 * Factory to manage RenderingEnv instance creation and dispose
 *
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class RenderingEnvFactory {

    private static RenderingEnvFactory instance;
    private WidgetManager widgetManager = new WidgetManager();
    private Map<Integer, RenderingEnv> clones = new WeakHashMap<>();
    private ActionController actionController;
    private CompositeContext globalContext;
    private ScriptEngine scriptEngine;

    public static RenderingEnvFactory getInstance() {
        if (instance == null) {
            instance = new RenderingEnvFactory();
        }
        return instance;
    }

    /**
     * Initializes principal rendering env and removes all clones
     *
     * @param env
     */
    public void initialize(RenderingEnv env) {
        env.clearSelection();
        env.clearMessages();
        env.setWidgetContext(createDefaultWidgetCtx());
        // remove existing widgets
        this.widgetManager.dispose();
        // remove previous clones if they exists
        for (RenderingEnv envClone : clones.values()) {
            dispose(envClone);
        }
        clones.clear();
    }

    public RenderingEnv create() {
        RenderingEnv env = new RenderingEnv(actionController);
        env.setFactory(this);
        env.setGlobalContext(globalContext);
        env.setWidgetManager(widgetManager);
        env.setScriptEngine(scriptEngine);
        env.setWidgetContext(createDefaultWidgetCtx());
        return env;
    }

    public RenderingEnv clone(RenderingEnv env) {
        RenderingEnv newEnv = create();
        newEnv.globalContext = env.globalContext;
        newEnv.widgetContext = env.widgetContext;
        newEnv.rootWidget = env.rootWidget;
        newEnv.viewDAG = env.viewDAG;
        newEnv.deferredViews = env.deferredViews;
        newEnv.userActionInterceptor = env.userActionInterceptor;
        newEnv.viewContext = env.viewContext;
        newEnv.formActivity = env.formActivity;
        newEnv.inputTypingDelay = env.inputTypingDelay;
        newEnv.inputDelayDisabled = env.inputDelayDisabled;
        newEnv.entity = env.entity;
        newEnv.messageMap = env.messageMap;
        newEnv.stateHolder = env.stateHolder;
        newEnv.widgetManager = env.widgetManager;
        this.clones.put(newEnv.hashCode(), newEnv);
        return newEnv;
    }

    public void dispose(RenderingEnv env) {
        env.globalContext = null;
        env.widgetContext = null;
        env.rootWidget = null;
        env.viewDAG = null;
        env.deferredViews.clear();
        env.deferredViews = null;
        env.userActionInterceptor = null;
        env.viewContext = null;
        env.formActivity = null;
        env.entity = null;
        env.messageMap.clear();
        env.messageMap = null;
        env.stateHolder = null;
        env.widgetManager = null;
    }

    public ActionController getActionController() {
        return actionController;
    }

    public void setActionController(ActionController actionController) {
        this.actionController = actionController;
    }

    private WidgetContext createDefaultWidgetCtx() {
        WidgetContext wCtx = new WidgetContext(EMPTY_WCTX_HOLDER);
        wCtx.addContext(globalContext);
        return wCtx;
    }

    public void setGlobalContext(CompositeContext globalContext) {
        this.globalContext = globalContext;
    }

    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    public void setScriptEngine(ScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
    }

    private static WidgetContextHolder EMPTY_WCTX_HOLDER = new WidgetContextHolder() {
        @Override
        public String getHolderId() {
            return null;
        }

        @Override
        public int getId() {
            return 0;
        }

        @Override
        public UIComponent getComponent() {
            return null;
        }

        @Override
        public String getComponentId() {
            return null;
        }

        @Override
        public Widget getWidget() {
            return null;
        }

        @Override
        public WidgetContext getWidgetContext() {
            return null;
        }

        @Override
        public WidgetContextHolder getHolder() {
            return null;
        }

        @Override
        public void dispose() {
        }

        @Override
        public void setWidgetContext(WidgetContext context) {
        }
    };
}
