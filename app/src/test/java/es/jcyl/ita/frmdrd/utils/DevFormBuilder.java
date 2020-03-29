package es.jcyl.ita.frmdrd.utils;
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

import es.jcyl.ita.frmdrd.forms.FormController;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Facade class with helper methods to automatically create data and fixtures for tests
 */
public class DevFormBuilder {


    public static FormController createFormController(UIForm form, Context viewContext) {
        UIView view = new UIView("v1");
        view.addChild(form);
        FormController fc = new FormController("c", "");
        fc.setEditView(view);
        fc.setViewContext(viewContext);
        return fc;
    }

}
