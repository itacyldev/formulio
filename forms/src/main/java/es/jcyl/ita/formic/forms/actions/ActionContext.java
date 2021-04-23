package es.jcyl.ita.formic.forms.actions;
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

import android.content.Context;

import es.jcyl.ita.formic.forms.controllers.FormController;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ActionContext {
    private FormController fc;
    private android.content.Context viewContext;

    public ActionContext(FormController fc, android.content.Context viewContext) {
        this.fc = fc;
        this.viewContext = viewContext;
    }

    public FormController getFc() {
        return fc;
    }

    public void setFc(FormController fc) {
        this.fc = fc;
    }

    public Context getViewContext() {
        return viewContext;
    }

    public void setViewContext(Context viewContext) {
        this.viewContext = viewContext;
    }

}
