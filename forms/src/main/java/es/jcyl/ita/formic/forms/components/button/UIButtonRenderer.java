package es.jcyl.ita.formic.forms.components.button;
/*
 * Copyright 2020  Rosa María Muñiz (mungarro@itacyl.es), ITACyL (http://www.itacyl.es).
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

import android.view.View;
import android.widget.Button;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.formic.forms.components.link.UIParam;
import es.jcyl.ita.formic.forms.el.JexlUtils;
import es.jcyl.ita.formic.forms.view.render.AbstractRenderer;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/**
 * @author Rosa María Muñiz (mungarro@itacyl.es)
 * <p>
 * Renders link component using android views
 */
public class UIButtonRenderer extends AbstractRenderer<UIButton, Widget<UIButton>> {

    @Override
    protected int getWidgetLayoutId(UIButton component) {
        return R.layout.widget_button;
    }

    @Override
    protected void composeWidget(RenderingEnv env, Widget<UIButton> widget) {
        UIButton component = widget.getComponent();

        Button addButton = widget.findViewById(R.id.btn_add);

        if(StringUtils.isNotBlank(component.getLabel())){
            addButton.setText(component.getLabel());
        }

        if (component.isReadOnly()) {
            addButton.setEnabled(false);
        } else {
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewUserActionInterceptor interceptor = env.getUserActionInterceptor();
                    if (interceptor != null) {
                        // TODO: FORMIC-202 UIButton utilizar método desde UserActionHelper
                        UserAction action = UserAction.navigate(component.getRoute(), component);
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
    }

    @Override
    protected void setupWidget(RenderingEnv env, Widget<UIButton> widget) {
    }

}
