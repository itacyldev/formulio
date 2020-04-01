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

import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.context.impl.OrderedCompositeContext;
import es.jcyl.ita.frmdrd.actions.ActionController;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.context.impl.FormContext;
import es.jcyl.ita.frmdrd.context.impl.MapCompositeContext;
import es.jcyl.ita.frmdrd.context.impl.UnPrefixedCompositeContext;

/**
 * Helper object using during the rendering process to give the renderers access to commons objects
 * needed during the view construction, like entity and view contexts and access to userAction
 * interceptors.
 * During the rendering, the ExecEnvironment gathers the form contexts to be bound later to the
 * GlobalContext, so access to all form context can be made with expressions like formId.entity.property
 * or formId.view.field.
 */
public class ExecEnvironment {

    CompositeContext globalContext;
    FormContext formContext;
    private ViewUserActionInterceptor userActionInterceptor;
    private CompositeContext combinedContext;
    private CompositeContext contextMap;

    public ExecEnvironment(CompositeContext globalContext, ActionController actionController) {
        this.globalContext = globalContext;
        userActionInterceptor = new ViewUserActionInterceptor(actionController);
        contextMap = new MapCompositeContext();
        if (globalContext == null) {
            throw new IllegalStateException("Global context is not properly set ExecEnvironment!.");
        }
    }

    /**
     * Clears composite context before starting the rendering process
     */
    public void initialize() {
        this.combinedContext = null;
        this.contextMap.clear();
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

    public void setFormContext(FormContext formContext) {
        // add current form context
        if (formContext == null) {
            throw new IllegalStateException("FormContext is not properly set in ExecEnvironment!.");
        }
        this.formContext = formContext;
        // combine form context with global context
        this.combinedContext = createCombinedContext(globalContext, formContext);
        // register form context
        contextMap.addContext(formContext);
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

    public CompositeContext getContextMap() {
        return contextMap;
    }
}
