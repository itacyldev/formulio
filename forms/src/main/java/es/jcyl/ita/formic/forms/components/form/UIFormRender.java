package es.jcyl.ita.formic.forms.components.form;
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

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.base.UIGroupBaseRenderer;
import es.jcyl.ita.formic.forms.controllers.operations.FormValidator;
import es.jcyl.ita.formic.forms.controllers.widget.BasicWidgetController;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIFormRender extends UIGroupBaseRenderer {
    public UIFormRender() {
        super(R.layout.widget_form);
    }

    @Override
    public void initGroup(RenderingEnv env, Widget root) {
        super.initGroup(env, root);
        BasicWidgetController controller = new BasicWidgetController(root);
        controller.setValidator(new FormValidator(env));
        ((FormWidget) root).setController(controller);
    }
}
