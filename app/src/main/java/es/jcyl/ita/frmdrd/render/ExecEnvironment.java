package es.jcyl.ita.frmdrd.render;
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
import es.jcyl.ita.frmdrd.context.impl.FormContext;
import es.jcyl.ita.frmdrd.interceptors.OnChangeFieldInterceptor;
import es.jcyl.ita.frmdrd.interceptors.OnSaveFormInterceptor;

/**
 * Provides a common access to objects that have to be bound to view components during the
 * rendering process, but will used during the view execution.
 */
public class ExecEnvironment {

    Context globalContext;
    FormContext formContext;
    CompositeContext combinedContext;
    private OnChangeFieldInterceptor changeInterceptor = new OnChangeFieldInterceptor();
    private OnSaveFormInterceptor saveFormInterceptor = new OnSaveFormInterceptor();

    public ExecEnvironment(Context globalContext) {
        this.globalContext = globalContext;
    }

    public Context getGlobalContext() {
        return globalContext;
    }

    public void setGlobalContext(Context globalContext) {
        this.globalContext = globalContext;
    }

    public FormContext getFormContext() {
        return formContext;
    }

    public void setFormContext(FormContext formContext) {
        this.formContext = formContext;
    }

    public OnChangeFieldInterceptor getChangeInterceptor() {
        return changeInterceptor;
    }

    public void setChangeInterceptor(OnChangeFieldInterceptor changeInterceptor) {
        this.changeInterceptor = changeInterceptor;
    }

    public OnSaveFormInterceptor getSaveFormInterceptor() {
        return saveFormInterceptor;
    }

    public void setSaveFormInterceptor(OnSaveFormInterceptor saveFormInterceptor) {
        this.saveFormInterceptor = saveFormInterceptor;
    }

    public CompositeContext getCombinedContext() {
        if (this.combinedContext == null) {
            combinedContext = new OrderedCompositeContext();
            if (globalContext == null) {
                throw new IllegalStateException("Global context is not property set ExecEnvironment!.");
            }
            combinedContext.addContext(globalContext);
            if (formContext == null) {
                throw new IllegalStateException("FormContext is not property set in ExecEnvironment!.");
            }
            combinedContext.addContext(formContext);
        }
        return combinedContext;
    }

}
