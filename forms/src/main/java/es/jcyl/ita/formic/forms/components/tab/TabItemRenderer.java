package es.jcyl.ita.formic.forms.components.tab;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.view.render.AbstractGroupRenderer;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/*
 * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class TabItemRenderer extends AbstractGroupRenderer<UITabItem, Widget<UITabItem>> {

    @Override
    protected int getWidgetLayoutId(UITabItem component) {
        return R.layout.widget_tab_fragment;
    }

    @Override
    protected void composeWidget(RenderingEnv env, Widget<UITabItem> widget) {
        // Do nothing
    }

    @Override
    protected boolean isAbleToShowNestedMessages() {
        return true;
    }
}
