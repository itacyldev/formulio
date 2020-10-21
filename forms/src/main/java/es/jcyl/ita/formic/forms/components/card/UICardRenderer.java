package es.jcyl.ita.formic.forms.components.card;
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

import android.content.res.Resources;
import android.widget.LinearLayout;
import android.widget.TextView;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.view.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.AbstractRenderer;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class UICardRenderer extends AbstractRenderer<UICard, Widget<UICard>> {
    @Override
    protected int getWidgetLayoutId() {
        return R.layout.card_template_1;
    }

    @Override
    protected void composeWidget(RenderingEnv env,  Widget<UICard> widget) {
        UICard card = widget.getComponent();
        UIComponent[] properties = card.getChildren();

        for(UIComponent property:properties){

        }


    }

    @Override
    protected void setupWidget(RenderingEnv env,  Widget<UICard> widget) {
        super.setupWidget(env, widget);

    }
}
