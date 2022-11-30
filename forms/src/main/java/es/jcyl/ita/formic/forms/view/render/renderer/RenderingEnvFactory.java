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

import es.jcyl.ita.formic.forms.actions.ActionController;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class RenderingEnvFactory {

    private static RenderingEnvFactory instance;
    private Map<Integer, RenderingEnv> clones = new WeakHashMap<>();

    public static RenderingEnvFactory getInstance() {
        if(instance == null){
            instance = new RenderingEnvFactory();
        }
        return instance;
    }

    public void initialize(RenderingEnv env){
        env.initialize();
        // remove previous clones if they exists
        for(RenderingEnv envClone: clones.values()){
            dispose(envClone);
        }
        clones.clear();
    }

    public RenderingEnv create(ActionController actionController) {
        RenderingEnv env = new RenderingEnv(actionController);
        env.setFactory(this);
        return env;
    }
    public RenderingEnv clone(RenderingEnv env) {
        RenderingEnv newEnv = new RenderingEnv();
        newEnv.globalContext = env.globalContext;
        newEnv.widgetContext = env.widgetContext;
        newEnv.rootWidget = env.rootWidget;
        newEnv.viewDAG = env.viewDAG;
        newEnv.deferredViews = env.deferredViews;
        newEnv.userActionInterceptor = env.userActionInterceptor;
        newEnv.viewContext = env.viewContext;
//        newEnv.formActivity = env.formActivity;
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
        env.messageMap = null;
        env.stateHolder = null;
        env.widgetManager = null;
    }


}
