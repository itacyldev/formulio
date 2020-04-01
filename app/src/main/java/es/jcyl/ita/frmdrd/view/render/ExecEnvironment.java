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
import es.jcyl.ita.crtrepo.context.Context;
import es.jcyl.ita.crtrepo.context.impl.OrderedCompositeContext;
import es.jcyl.ita.frmdrd.actions.ActionController;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.context.impl.FormContext;

/**
 * Provides a common access to objects that have to be bound to view components during the
 * rendering process, but will used during the view execution: context and event interceptor.
 */
public class ExecEnvironment {

    Context globalContext;
    FormContext formContext;
    private ViewUserActionInterceptor userActionInterceptor;
    private CompositeContext combinedContext;

    public ExecEnvironment(Context globalContext, ActionController actionController) {
        this.globalContext = globalContext;
        userActionInterceptor = new ViewUserActionInterceptor(actionController);
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
        this.formContext = formContext;
        this.combinedContext = createCombinedContext(globalContext, formContext);
    }

    public ViewUserActionInterceptor getUserActionInterceptor() {
        return userActionInterceptor;
    }

    private CompositeContext createCombinedContext(Context globalContext, FormContext fContext) {
        CompositeContext combinedContext = new OrderedCompositeContext();
        if (globalContext == null) {
            throw new IllegalStateException("Global context is not property set ExecEnvironment!.");
        }
        combinedContext.addContext(globalContext);
        if (fContext == null) {
            throw new IllegalStateException("FormContext is not property set in ExecEnvironment!.");
        }
        combinedContext.addContext(fContext);
        return combinedContext;

    }
}
