package es.jcyl.ita.frmdrd.render;
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
import android.view.View;

import es.jcyl.ita.frmdrd.configuration.DataBindings;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.view.ExecEnvironment;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public abstract class BaseRenderer implements Renderer {

    public final View render(Context viewContext, ExecEnvironment env, UIComponent component) {
        View baseView = createBaseView(viewContext, env, component);
        // check render condition
        boolean isRenderable = checkRenderConditions(component);
        baseView.setVisibility(isRenderable ? View.VISIBLE : View.INVISIBLE);
        if (!isRenderable) {
            return baseView;
        }
        setupView(baseView, env, component);

        bindField(baseView, component);
        return baseView;
    }

    protected boolean checkRenderConditions(UIComponent component) {
        String renderCondition = component.getRenderCondition();

//        boolean render = true;
//        if (StringUtils.isNotEmpty(renderCondition)) {
//            render = this.validateCondition(renderCondition, field.getId());
//        }
        return !("false".equalsIgnoreCase(renderCondition));
    }

    protected void bindField(View baseView, UIComponent component) {
        DataBindings.registerView(component.getId(), baseView);
    }

    /**
     * Create a base view from context and component information to view used as placeholder in the form view
     *
     * @param viewContext
     * @param env
     * @param component
     * @return
     */
    protected abstract View createBaseView(Context viewContext, ExecEnvironment env, UIComponent component);

    protected abstract void setupView(View baseView, ExecEnvironment env, UIComponent component);
}
