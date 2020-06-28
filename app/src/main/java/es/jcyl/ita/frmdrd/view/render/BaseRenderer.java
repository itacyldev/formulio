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

import android.view.View;

import androidx.annotation.Nullable;

import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public abstract class BaseRenderer<V extends View, C extends UIComponent> implements Renderer<C> {


    public final View render(RenderingEnv env, C component) {
        V baseView = createBaseView(env, component);
        // check render condition
        boolean isRendered = component.isRendered(env.getContext());
        baseView.setVisibility(isRendered ? View.VISIBLE : View.GONE);
        if (!isRendered) {
            return baseView;
        }
        setupView(env, baseView, component);
        return baseView;
    }

    /**
     * Create a base view from context and component information to view used as placeholder in the form view
     *
     * @param env
     * @param component
     * @return
     */
    protected abstract V createBaseView(RenderingEnv env, C component);

    protected abstract void setupView(RenderingEnv env, V baseView, C component);

    /**
     * Calculates the tag for the GroupView component that contains all the input, so when a partial
     * re-render is applied, the hole component can be easily found in the view.
     * The base view will be tagged as form:id and the input view below this as form:id>input
     *
     * @return
     */
    protected String getBaseViewTag(C c) {
        UIForm form = c.getParentForm();
        String formId = (form == null) ? "root" : form.getId();
        return formId + ":" + c.getId();
    }

    /**
     * Tries to retrieve the component value first accessing the form context and then using
     * global context
     *
     * @param component
     * @param env
     * @return
     */
    protected <T> T getValue(C component, RenderingEnv env,@Nullable Class<T> clazz) {
        Object value = component.getValue(env.getContext());
        if (value == null) {
            return handleNullValue(component);
        }
        if (clazz == null) {
            return (T) value;
        } else {
            return (T) ConvertUtils.convert(value, clazz);
        }
    }

    protected <T> T handleNullValue(C component) {
        return null;
    }

}
