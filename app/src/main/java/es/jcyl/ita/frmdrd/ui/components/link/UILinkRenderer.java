package es.jcyl.ita.frmdrd.ui.components.link;
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

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.RandomUtils;

import java.io.Serializable;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.el.JexlUtils;
import es.jcyl.ita.frmdrd.view.render.AbstractRenderer;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;
import es.jcyl.ita.frmdrd.view.widget.Widget;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Renders link component using android views
 */
public class UILinkRenderer extends AbstractRenderer<UILink, Widget<UILink>> {

    @Override
    protected int getWidgetLayoutId() {
        return R.layout.widget_link;
    }

    @Override
    protected void composeWidget(RenderingEnv env, Widget<UILink> widget) {
        UILink component = widget.getComponent();
        TextView linkView = widget.findViewById(R.id.textViewLink);
        linkView.setId(RandomUtils.nextInt());

        String value = getComponentValue(env, component, String.class);
        linkView.setText(Html.fromHtml(String.format("<u>%s</u>", value)));
        linkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUserActionInterceptor interceptor = env.getUserActionInterceptor();
                if (interceptor != null) {
                    UserAction action = UserAction.navigate(env.getViewContext(), component, component.getRoute());
                    if (component.hasParams()) {
                        for (UIParam param : component.getParams()) {
                            Object value = JexlUtils.eval(env.getContext(), param.getValue());
                            action.addParam(param.getName(), (Serializable) value);
                        }
                    }
                    interceptor.doAction(action);
                }
            }
        });
    }

    @Override
    protected void setupWidget(RenderingEnv env, Widget<UILink> widget) {
    }

}
