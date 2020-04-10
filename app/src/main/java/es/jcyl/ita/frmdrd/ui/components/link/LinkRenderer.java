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

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.RandomUtils;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.view.ViewHelper;
import es.jcyl.ita.frmdrd.view.render.BaseRenderer;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Renders link component using android views
 */
public abstract class LinkRenderer extends BaseRenderer<UILink> {

    @Override
    protected View createBaseView(RenderingEnv env, UILink component) {
        LinearLayout baseView = ViewHelper.inflate(env.getViewContext(),
                R.layout.component_placeholders, LinearLayout.class);
        TextView linkView = (TextView) baseView.findViewById(R.id.textViewLink);
        // set randomId
        linkView.setId(RandomUtils.nextInt());
        return linkView;
    }

    @Override
    protected void setupView(RenderingEnv env, View baseView, UILink component) {
        TextView linkView = (TextView) baseView;
        String value = getValue(component, env, String.class);
        linkView.setText(String.format("<a>%s</a>", value));
        linkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUserActionInterceptor interceptor = env.getUserActionInterceptor();
                if (interceptor != null) {
//                    UserAction.navigate()
//                    interceptor.doAction(new UserAction(env.getViewContext(), component, ActionType.NAVIGATE.name()));
                }
            }
        });

    }


}
