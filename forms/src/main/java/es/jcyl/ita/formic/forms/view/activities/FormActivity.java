package es.jcyl.ita.formic.forms.view.activities;
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

import android.app.Activity;
import android.view.ViewGroup;

import es.jcyl.ita.formic.forms.controllers.FormController;
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;

/**
 * Interface to provide access to Android Activity from rendering environment and to register
 * current activity in MainController/router.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public interface FormActivity<F extends FormController> {

    void setFormController(F formController);

    void setRouter(Router router);

    void setRenderingEnv(RenderingEnv env);

    Activity getActivity();

    ViewGroup getContentView();

    void registerCallBackForActivity(ActivityResultCallBack callback);
}