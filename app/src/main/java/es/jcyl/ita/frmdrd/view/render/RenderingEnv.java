package es.jcyl.ita.frmdrd.view.render;
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

import android.view.View;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.context.impl.OrderedCompositeContext;
import es.jcyl.ita.frmdrd.MainController;
import es.jcyl.ita.frmdrd.actions.ActionController;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.context.impl.FormContext;
import es.jcyl.ita.frmdrd.view.dag.DAGNode;
import es.jcyl.ita.frmdrd.view.dag.ViewDAG;

/**
 * Context storing object used during the rendering process to give the renderers access to commons objects
 * needed during the view construction, like entity and view contexts, access to userAction
 * interceptors and a temporary store like deffered view elements.
 * During the rendering, the ExecEnvironment gathers the form contexts to be bound later to the
 * GlobalContext, so access to all form context can be made with expressions like formId.entity.property
 * or formId.view.field.
 */
public class RenderingEnv {

    CompositeContext globalContext;
    FormContext formContext;

    private ViewUserActionInterceptor userActionInterceptor;
    private CompositeContext combinedContext;
    private List<FormContext> currentFormContexts;
    //    private CompositeContext contextMap;
    private Map<String, DeferredView> deferredViews;
    private ViewDAG viewDAG;

    public RenderingEnv(CompositeContext globalContext, ActionController actionController) {
        this.globalContext = globalContext;
        userActionInterceptor = new ViewUserActionInterceptor(actionController);
        currentFormContexts = new ArrayList<>();
        if (globalContext == null) {
            throw new IllegalStateException("Global context mustn't be null!.");
        }
    }

    /**
     * Clears composite context before starting the rendering process
     */
    public void initialize() {
        this.combinedContext = null;
        // remove last context from global context
        if (!currentFormContexts.isEmpty()) {
            for (FormContext formContext : currentFormContexts) {
                this.globalContext.removeContext(formContext);
            }
        }
        currentFormContexts.clear();
    }

    public CompositeContext getContext() {
        return combinedContext;
    }

    public void disableInterceptors() {
        this.userActionInterceptor.setDisabled(true);
    }

    public void enableInterceptors() {
        this.userActionInterceptor.setDisabled(false);
    }

    public FormContext getFormContext() {
        return formContext;
    }

    /**
     * Every time a form starts the rendering, the RenderEnv stores the context and links it with
     * the formId to the global context. So relative access can be done inside the form elements,
     * but also absolute access inter-form can be achieve throught the global context references.
     *
     * @param formContext
     */
    public void setFormContext(FormContext formContext) {
        if (formContext == null) {
            throw new IllegalStateException("FormContext mustn't be null!.");
        }
        this.formContext = formContext;
        // add form to context with full id
        this.globalContext.addContext(formContext);
        // register
        this.combinedContext = createCombinedContext(globalContext, formContext);
        // register this FormContext
        currentFormContexts.add(formContext);
    }

    public ViewUserActionInterceptor getUserActionInterceptor() {
        return userActionInterceptor;
    }

    private CompositeContext createCombinedContext(CompositeContext globalContext, FormContext fContext) {
        CompositeContext combinedContext = new OrderedCompositeContext();
        combinedContext.addContext(globalContext);
        combinedContext.addContext(fContext);
        return combinedContext;
    }

    public void addDeferred(String componentId, DeferredView view) {
        if (this.deferredViews == null) {
            this.deferredViews = new HashMap<>();
        }
        this.deferredViews.put(componentId, view);
    }

    public Map<String, DeferredView> getDeferredViews() {
        return deferredViews;
    }

    public void setViewDAG(ViewDAG viewDAG) {
        this.viewDAG = viewDAG;
    }

    public ViewDAG getViewDAG() {
        return viewDAG;
    }

    public View getViewRoot() {
        MainController mc = MainController.getInstance();
        return mc.getViewRoot();
    }
}
