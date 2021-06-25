package es.jcyl.ita.formic.forms;
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
import android.content.Intent;

import es.jcyl.ita.formic.forms.controllers.ViewController;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Controls navigation between forms and the main loading and rendering flow.
 * <p>
 * Shares the context, AndroidContext and current components and view data with action handlers
 * to implement user actions.
 */

public class MainControllerMock extends MainController {

    private ViewController viewController;

    public MainControllerMock() {
    }

    @Override
    protected void initActivity(Context context) {
        Class activityClazz = getViewImpl(this.getViewController());
        // Start activity to get Android context
        Intent intent = new Intent(context, activityClazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Overrides MainController method to return a hardcoded instance
     *
     * @param formId
     * @return
     */
    @Override
    protected ViewController getViewController(String formId) {
        if (viewController != null) {
            return viewController;
        } else {
            return super.getViewController(formId);
        }
    }

    public void setViewController(ViewController viewController) {
        this.viewController = viewController;
        MainController mc = MainController.getInstance();
        this.viewController.setMc(mc);
        mc.setFormController(viewController);
    }
}
