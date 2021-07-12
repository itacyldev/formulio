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

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.forms.controllers.ViewController;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ActionContext {
    private ViewController viewController;
    private android.content.Context viewContext;
    private Map<String, Object> flashCache;

    public ActionContext(ViewController viewController, android.content.Context viewContext) {
        this.viewController = viewController;
        this.viewContext = viewContext;
    }

    public ViewController getViewController() {
        return viewController;
    }

    public void setViewController(ViewController viewController) {
        this.viewController = viewController;
    }

    public Context getViewContext() {
        return viewContext;
    }

    public void setViewContext(Context viewContext) {
        this.viewContext = viewContext;
    }

    public void setAttribute(String attName, Object value) {
        if (this.flashCache == null) {
            this.flashCache = new HashMap<>();
        }
        this.flashCache.put(attName, value);
    }

    public Object getAttribute(String attName) {
        return (this.flashCache == null) ? null : this.flashCache.get(attName);
    }
}
