package es.jcyl.ita.frmdrd.view.render;
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

import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.view.converters.ViewValueConverterFactory;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public abstract class BaseRenderer implements Renderer {

    protected ViewValueConverterFactory convFactory = ViewValueConverterFactory.getInstance();

    public final View render(Context viewContext, RenderingEnv env, UIComponent component) {
        View baseView = createBaseView(viewContext, env, component);
        // check render condition
        boolean isRendered = component.isRendered(env.getContext());
        baseView.setVisibility(isRendered ? View.VISIBLE : View.GONE);
        if (!isRendered) {
            return baseView;
        }
        setupView(baseView, env, component);
        return baseView;
    }

    /**
     * Create a base view from context and component information to view used as placeholder in the form view
     *
     * @param viewContext
     * @param env
     * @param component
     * @return
     */
    protected abstract View createBaseView(Context viewContext, RenderingEnv env, UIComponent component);

    protected abstract void setupView(View baseView, RenderingEnv env, UIComponent component);

    /**
     * Calculates the tag for the GroupView component that contains all the input, so when a partial
     * re-render is applied, the hole component can be easily found in the view.
     * The base view will be tagged as form:id and the input view below this as form:id>input
     *
     * @return
     */
    protected String getBaseViewTag(UIComponent c) {
        UIForm form = c.getParentForm();
        String formId = (form == null) ? "root" : form.getId();
        return formId + ":" + c.getId();
    }


}
